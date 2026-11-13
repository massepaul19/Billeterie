package cm.billetterie.service;

import cm.billetterie.dto.AnnulationReponse;
import cm.billetterie.dto.BilletReponse;
import cm.billetterie.dto.VoyageResume;
import cm.billetterie.entity.*;
import cm.billetterie.entity.enums.*;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BilletService {

    private static final ZoneId ZONE = ZoneId.of("Africa/Douala");

    private final BilletRepository billets;
    private final ReservationRepository reservations;
    private final ClientRepository clients;
    private final VoyageRepository voyages;
    private final RemboursementRepository remboursements;
    private final VoyageAssembleur assembleur;
    private final int delaiLimiteHeures;
    private final int retenuePourcentage;

    public BilletService(BilletRepository billets, ReservationRepository reservations, ClientRepository clients,
                         VoyageRepository voyages, RemboursementRepository remboursements,
                         VoyageAssembleur assembleur,
                         @Value("${billetterie.annulation.delai-limite-heures}") int delaiLimiteHeures,
                         @Value("${billetterie.annulation.retenue-pourcentage}") int retenuePourcentage) {
        this.billets = billets;
        this.reservations = reservations;
        this.clients = clients;
        this.voyages = voyages;
        this.remboursements = remboursements;
        this.assembleur = assembleur;
        this.delaiLimiteHeures = delaiLimiteHeures;
        this.retenuePourcentage = retenuePourcentage;
    }

    @Transactional
    public BilletReponse obtenir(String code) {
        Billet billet = trouver(code);
        if (billet.getStatut() == StatutBillet.VALIDE && billet.getValideJusquA() != null
                && billet.getValideJusquA().isBefore(LocalDateTime.now(ZONE))) {
            billet.setStatut(StatutBillet.EXPIRE);
            billets.save(billet);
        }
        Reservation res = reservations.findById(billet.getReservationId()).orElseThrow(this::introuvable);
        Client client = clients.findById(res.getClientId()).orElseThrow(this::introuvable);
        Voyage voyage = voyages.findById(res.getVoyageId()).orElseThrow(this::introuvable);
        VoyageResume resume = assembleur.chargerUn(voyage).map(assembleur::resume).orElseThrow(this::introuvable);
        return new BilletReponse(billet.getCodeUnique(), billet.getStatut(), billet.getValideJusquA(),
                new BilletReponse.ClientInfo(client.getNom(), client.getPrenom()), resume);
    }

    /** RG9 : annulation par le client jusqu'à 2 h avant le départ, remboursement moins 5 %. */
    @Transactional
    public AnnulationReponse annuler(String code) {
        Billet billet = trouver(code);
        if (billet.getStatut() != StatutBillet.VALIDE) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "BILLET_INVALIDE",
                    "Ce billet n'est plus valide, il ne peut pas être annulé.");
        }
        Reservation res = reservations.findById(billet.getReservationId()).orElseThrow(this::introuvable);
        Voyage voyage = voyages.findById(res.getVoyageId()).orElseThrow(this::introuvable);
        LocalDateTime limite = voyage.getDateHeureDepart().minusHours(delaiLimiteHeures);
        if (LocalDateTime.now(ZONE).isAfter(limite)) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "ANNULATION_TARDIVE",
                    "Annulation impossible moins de " + delaiLimiteHeures + " h avant le départ.");
        }

        billet.setStatut(StatutBillet.ANNULE);
        billets.save(billet);
        res.setStatut(StatutReservation.ANNULEE);
        reservations.save(res);
        voyages.libererUnePlace(voyage.getId());

        int retenue = (int) Math.round(res.getMontant() * retenuePourcentage / 100.0);
        Remboursement remb = new Remboursement();
        remb.setReservationId(res.getId());
        remb.setMontant(res.getMontant() - retenue);
        remb.setMotif("Annulation par le client");
        remb.setStatut(StatutRemboursement.DEMANDE);
        remb.setCreeLe(LocalDateTime.now(ZONE));
        remb = remboursements.save(remb);
        return new AnnulationReponse(new AnnulationReponse.RemboursementInfo(remb.getMontant(), remb.getStatut()));
    }

    private Billet trouver(String code) {
        return billets.findByCodeUnique(code == null ? "" : code.trim().toUpperCase()).orElseThrow(this::introuvable);
    }

    private ApiException introuvable() {
        return new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Billet introuvable.");
    }
}
