package cm.billetterie.service;

import cm.billetterie.dto.AnnulationVoyageReponse;
import cm.billetterie.dto.VoyageAgenceReponse;
import cm.billetterie.dto.VoyageAgenceRequete;
import cm.billetterie.entity.*;
import cm.billetterie.entity.enums.*;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgenceVoyageService {

    private static final ZoneId ZONE = ZoneId.of("Africa/Douala");

    private final VoyageAgenceRepository voyages;
    private final TrajetRepository trajets;
    private final VehiculeRepository vehicules;
    private final GareRepository gares;
    private final ReservationAgenceRepository reservations;
    private final BilletRepository billets;
    private final RemboursementRepository remboursements;

    public AgenceVoyageService(VoyageAgenceRepository voyages, TrajetRepository trajets,
                               VehiculeRepository vehicules, GareRepository gares,
                               ReservationAgenceRepository reservations, BilletRepository billets,
                               RemboursementRepository remboursements) {
        this.voyages = voyages;
        this.trajets = trajets;
        this.vehicules = vehicules;
        this.gares = gares;
        this.reservations = reservations;
        this.billets = billets;
        this.remboursements = remboursements;
    }

    public List<VoyageAgenceReponse> lister(Long agenceId) {
        return voyages.findByAgenceIdOrderByDateHeureDepartDesc(agenceId).stream().map(this::vers).toList();
    }

    @Transactional
    public VoyageAgenceReponse creer(Long agenceId, VoyageAgenceRequete r) {
        verifierDate(r);
        trajets.findByIdAndAgenceId(r.trajetId(), agenceId).orElseThrow(() -> validation("Trajet inconnu pour cette agence."));
        Vehicule v = vehicules.findByIdAndAgenceId(r.vehiculeId(), agenceId)
                .orElseThrow(() -> validation("Véhicule inconnu pour cette agence."));
        Voyage x = new Voyage();
        x.setAgenceId(agenceId);
        x.setTrajetId(r.trajetId());
        x.setVehiculeId(r.vehiculeId());
        x.setDateHeureDepart(r.dateHeureDepart());
        x.setPrix(r.prix());
        x.setPlacesRestantes(v.getNbPlaces());
        x.setStatut(StatutVoyage.PROGRAMME);
        return vers(voyages.save(x));
    }

    @Transactional
    public VoyageAgenceReponse modifier(Long agenceId, Long id, VoyageAgenceRequete r) {
        Voyage x = trouver(agenceId, id);
        if (x.getStatut() == StatutVoyage.PARTI || x.getStatut() == StatutVoyage.ANNULE) {
            throw new ApiException(HttpStatus.CONFLICT, "CONFLIT", "Ce voyage ne peut plus être modifié.");
        }
        boolean actives = reservations.findByVoyageId(id).stream()
                .anyMatch(res -> res.getStatut() == StatutReservation.EN_ATTENTE
                        || res.getStatut() == StatutReservation.PAYEE);
        boolean structure = !r.trajetId().equals(x.getTrajetId()) || !r.vehiculeId().equals(x.getVehiculeId())
                || !r.dateHeureDepart().equals(x.getDateHeureDepart());
        if (actives && structure) {
            throw new ApiException(HttpStatus.CONFLICT, "CONFLIT",
                    "Des réservations existent : seul le prix peut être modifié.");
        }
        if (structure) {
            verifierDate(r);
            trajets.findByIdAndAgenceId(r.trajetId(), agenceId).orElseThrow(() -> validation("Trajet inconnu pour cette agence."));
            Vehicule v = vehicules.findByIdAndAgenceId(r.vehiculeId(), agenceId)
                    .orElseThrow(() -> validation("Véhicule inconnu pour cette agence."));
            x.setTrajetId(r.trajetId());
            x.setVehiculeId(r.vehiculeId());
            x.setDateHeureDepart(r.dateHeureDepart());
            x.setPlacesRestantes(v.getNbPlaces());
            x.setStatut(StatutVoyage.PROGRAMME);
        }
        x.setPrix(r.prix());
        return vers(voyages.save(x));
    }

    @Transactional
    public VoyageAgenceReponse changerPrix(Long agenceId, Long id, Integer prix) {
        Voyage x = trouver(agenceId, id);
        x.setPrix(prix); // les réservations déjà faites gardent leur montant
        return vers(voyages.save(x));
    }

    /** Annule le voyage : réservations payées -> billets annulés + remboursement à 100 %. */
    @Transactional
    public AnnulationVoyageReponse annuler(Long agenceId, Long id) {
        Voyage x = trouver(agenceId, id);
        if (x.getStatut() == StatutVoyage.ANNULE || x.getStatut() == StatutVoyage.PARTI) {
            throw new ApiException(HttpStatus.CONFLICT, "CONFLIT", "Ce voyage ne peut plus être annulé.");
        }
        LocalDateTime maintenant = LocalDateTime.now(ZONE);
        int nb = 0;
        long total = 0;
        for (Reservation res : reservations.findByVoyageId(id)) {
            if (res.getStatut() == StatutReservation.PAYEE) {
                res.setStatut(StatutReservation.ANNULEE);
                reservations.save(res);
                billets.findByReservationId(res.getId()).ifPresent(b -> {
                    b.setStatut(StatutBillet.ANNULE);
                    billets.save(b);
                });
                Remboursement rb = new Remboursement();
                rb.setReservationId(res.getId());
                rb.setMontant(res.getMontant());
                rb.setMotif("Voyage annulé par l'agence");
                rb.setStatut(StatutRemboursement.DEMANDE);
                rb.setCreeLe(maintenant);
                remboursements.save(rb);
                nb++;
                total += res.getMontant();
            } else if (res.getStatut() == StatutReservation.EN_ATTENTE) {
                res.setStatut(StatutReservation.EXPIREE);
                reservations.save(res);
            }
        }
        x.setStatut(StatutVoyage.ANNULE);
        voyages.save(x);
        return new AnnulationVoyageReponse(nb, total);
    }

    private Voyage trouver(Long agenceId, Long id) {
        return voyages.findByIdAndAgenceId(id, agenceId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Voyage introuvable."));
    }

    private void verifierDate(VoyageAgenceRequete r) {
        if (!r.dateHeureDepart().isAfter(LocalDateTime.now(ZONE))) {
            throw validation("La date de départ doit être dans le futur.");
        }
    }

    private VoyageAgenceReponse vers(Voyage x) {
        Trajet t = trajets.findById(x.getTrajetId()).orElse(null);
        Vehicule v = vehicules.findById(x.getVehiculeId()).orElse(null);
        return new VoyageAgenceReponse(x.getId(), x.getTrajetId(),
                t == null ? null : nomGare(t.getGareDepartId()), t == null ? null : nomGare(t.getGareArriveeId()),
                x.getVehiculeId(), v == null ? null : v.getImmatriculation(), v == null ? null : v.getNbPlaces(),
                x.getDateHeureDepart(), x.getPrix(), x.getPlacesRestantes(), x.getStatut(), x.getModeleId());
    }

    private String nomGare(Long id) {
        return gares.findById(id).map(Gare::getNom).orElse(null);
    }

    private static ApiException validation(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", message);
    }
}
