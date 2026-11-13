package cm.billetterie.service;

import cm.billetterie.dto.PaiementReponse;
import cm.billetterie.dto.PaiementRequete;
import cm.billetterie.entity.*;
import cm.billetterie.entity.enums.*;
import cm.billetterie.exception.ApiException;
import cm.billetterie.payment.PaiementProvider;
import cm.billetterie.payment.PaiementResultat;
import cm.billetterie.repository.*;
import cm.billetterie.util.CodeBilletGenerator;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cm.billetterie.event.BilletPayeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaiementService {

    private static final ZoneId ZONE = ZoneId.of("Africa/Douala");
    private static final double TAUX_PAR_DEFAUT = 0.007;

    private final ReservationRepository reservations;
    private final PaiementRepository paiements;
    private final BilletRepository billets;
    private final VoyageRepository voyages;
    private final AgenceRepository agences;
    private final RappelRepository rappels;
    private final ParametreRepository parametres;
    private final Map<String, PaiementProvider> providers;
    private final ApplicationEventPublisher evenements;

    public PaiementService(ReservationRepository reservations, PaiementRepository paiements,
                           BilletRepository billets, VoyageRepository voyages, AgenceRepository agences,
                           RappelRepository rappels, ParametreRepository parametres,
                           Map<String, PaiementProvider> providers,
                           ApplicationEventPublisher evenements) {
        this.reservations = reservations;
        this.paiements = paiements;
        this.billets = billets;
        this.voyages = voyages;
        this.agences = agences;
        this.rappels = rappels;
        this.parametres = parametres;
        this.providers = providers;
        this.evenements = evenements;
    }

    @Transactional
    public PaiementReponse payer(PaiementRequete req) {
        Reservation res = reservations.findById(req.reservationId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Réservation introuvable."));
        LocalDateTime maintenant = LocalDateTime.now(ZONE);

        if (res.getStatut() == StatutReservation.PAYEE) return dejaPayee(res);
        if (res.getStatut() != StatutReservation.EN_ATTENTE
                || (res.getExpireA() != null && res.getExpireA().isBefore(maintenant))) {
            throw new ApiException(HttpStatus.CONFLICT, "RESERVATION_EXPIREE",
                    "Cette réservation a expiré. Recommencez la réservation.");
        }

        PaiementProvider provider = providers.get(req.fournisseur().name());
        if (req.fournisseur() != FournisseurPaiement.SIMULE || provider == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION",
                    "Seul le paiement simulé est disponible pour le moment.");
        }

        PaiementResultat resultat = provider.payer(res, req.telephonePaiement(), req.operateur());

        Paiement paiement = new Paiement();
        paiement.setReservationId(res.getId());
        paiement.setFournisseur(req.fournisseur());
        paiement.setReference(resultat.reference());
        paiement.setMontant(res.getMontant());
        paiement.setStatut(resultat.statut());
        paiement.setCreeLe(maintenant);
        paiement = paiements.save(paiement);

        if (resultat.statut() == StatutPaiement.REUSSI) {
            res.setStatut(StatutReservation.PAYEE);
            res.setCommission((int) Math.round(res.getMontant() * tauxCommission())); // RG12
            reservations.save(res);
            Billet billet = creerBillet(res, maintenant);
            evenements.publishEvent(new BilletPayeEvent(billet.getId()));
            return new PaiementReponse(paiement.getId(), paiement.getStatut(), paiement.getReference(),
                    res.getStatut(), billet.getCodeUnique());
        }

        // Échec : réservation EXPIREE et place libérée (RG4)
        res.setStatut(StatutReservation.EXPIREE);
        reservations.save(res);
        voyages.libererUnePlace(res.getVoyageId());
        return new PaiementReponse(paiement.getId(), paiement.getStatut(), paiement.getReference(),
                res.getStatut(), null);
    }

    private PaiementReponse dejaPayee(Reservation res) {
        Paiement p = paiements.findFirstByReservationIdOrderByIdDesc(res.getId()).orElse(null);
        Billet b = billets.findByReservationId(res.getId()).orElse(null);
        return new PaiementReponse(p == null ? null : p.getId(), StatutPaiement.REUSSI,
                p == null ? null : p.getReference(), StatutReservation.PAYEE,
                b == null ? null : b.getCodeUnique());
    }

    private Billet creerBillet(Reservation res, LocalDateTime maintenant) {
        Voyage voyage = voyages.findById(res.getVoyageId()).orElseThrow();

        String code;
        int essais = 0;
        do {
            code = CodeBilletGenerator.nouveauCode();
        } while (billets.findByCodeUnique(code).isPresent() && ++essais < 10);

        Billet billet = new Billet();
        billet.setReservationId(res.getId());
        billet.setCodeUnique(code);
        billet.setStatut(StatutBillet.VALIDE);
        billet.setValideJusquA(voyage.getDateHeureDepart());
        billet.setReutilise(false);
        billet = billets.save(billet);

        planifierRappels(billet, voyage, maintenant);
        return billet;
    }

    /** Un rappel par délai configuré par l'agence (ex. "24,2" = 24 h et 2 h avant le départ). */
    private void planifierRappels(Billet billet, Voyage voyage, LocalDateTime maintenant) {
        Agence agence = agences.findById(voyage.getAgenceId()).orElse(null);
        if (agence == null || agence.getDelaisRappelHeures() == null || voyage.getDateHeureDepart() == null) return;
        Matcher m = Pattern.compile("\\d+").matcher(agence.getDelaisRappelHeures());
        while (m.find()) {
            LocalDateTime quand = voyage.getDateHeureDepart().minusHours(Long.parseLong(m.group()));
            if (quand.isAfter(maintenant)) {
                Rappel rappel = new Rappel();
                rappel.setBilletId(billet.getId());
                rappel.setEnvoyerA(quand);
                rappel.setCanal(CanalRappel.EMAIL);
                rappel.setEnvoye(false);
                rappels.save(rappel);
            }
        }
    }

    private double tauxCommission() {
        try {
            return parametres.findById("commission_taux")
                    .map(p -> Double.parseDouble(p.getValeur())).orElse(TAUX_PAR_DEFAUT);
        } catch (NumberFormatException e) {
            return TAUX_PAR_DEFAUT;
        }
    }
}
