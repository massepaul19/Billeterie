package cm.billetterie.service;

import cm.billetterie.dto.BilletAgenceReponse;
import cm.billetterie.entity.*;
import cm.billetterie.entity.enums.StatutBillet;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgenceVerificationService {

    private static final int MAX_RESULTATS = 50;

    private final BilletRepository billets;
    private final ReservationAgenceRepository reservations;
    private final VoyageAgenceRepository voyages;
    private final ClientRepository clients;
    private final TrajetRepository trajets;
    private final GareRepository gares;

    public AgenceVerificationService(BilletRepository billets, ReservationAgenceRepository reservations,
                                     VoyageAgenceRepository voyages, ClientRepository clients,
                                     TrajetRepository trajets, GareRepository gares) {
        this.billets = billets;
        this.reservations = reservations;
        this.voyages = voyages;
        this.clients = clients;
        this.trajets = trajets;
        this.gares = gares;
    }

    /** Un billet VALIDE de cette agence passe à UTILISE ; sinon 422 BILLET_INVALIDE. */
    @Transactional
    public BilletAgenceReponse verifier(Long agenceId, String code) {
        Billet b = billets.findByCodeUnique(code.trim()).orElseThrow(this::introuvable);
        Reservation res = reservations.findById(b.getReservationId()).orElseThrow(this::introuvable);
        Voyage v = voyages.findByIdAndAgenceId(res.getVoyageId(), agenceId).orElseThrow(this::introuvable);
        if (b.getStatut() != StatutBillet.VALIDE) {
            String raison = switch (b.getStatut()) {
                case UTILISE -> "Ce billet a déjà été utilisé.";
                case EXPIRE -> "Ce billet est expiré.";
                case ANNULE -> "Ce billet est annulé.";
                default -> "Ce billet n'est pas valide.";
            };
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "BILLET_INVALIDE", raison);
        }
        b.setStatut(StatutBillet.UTILISE);
        billets.save(b);
        return vers(b, res, v);
    }

    /** Recherche par nom, prénom ou téléphone du client, limitée aux voyages de l'agence. */
    public List<BilletAgenceReponse> rechercher(Long agenceId, String q) {
        if (q == null || q.trim().length() < 2) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", "Saisissez au moins 2 caractères.");
        }
        List<Long> ids = clients.rechercher(q.trim()).stream().map(Client::getId).toList();
        List<BilletAgenceReponse> sortie = new ArrayList<>();
        if (ids.isEmpty()) return sortie;
        for (Reservation res : reservations.findByClientIdIn(ids)) {
            if (sortie.size() >= MAX_RESULTATS) break;
            Voyage v = voyages.findByIdAndAgenceId(res.getVoyageId(), agenceId).orElse(null);
            if (v == null) continue;
            billets.findByReservationId(res.getId()).ifPresent(b -> sortie.add(vers(b, res, v)));
        }
        return sortie;
    }

    private BilletAgenceReponse vers(Billet b, Reservation res, Voyage v) {
        Client c = res.getClientId() == null ? null : clients.findById(res.getClientId()).orElse(null);
        Trajet t = trajets.findById(v.getTrajetId()).orElse(null);
        return new BilletAgenceReponse(b.getCodeUnique(), b.getStatut(), b.getValideJusquA(),
                c == null ? null : new BilletAgenceReponse.ClientInfo(c.getNom(), c.getPrenom(), c.getTelephone()),
                new BilletAgenceReponse.VoyageInfo(v.getId(),
                        t == null ? null : nomGare(t.getGareDepartId()),
                        t == null ? null : nomGare(t.getGareArriveeId()),
                        v.getDateHeureDepart()));
    }

    private String nomGare(Long id) {
        return gares.findById(id).map(Gare::getNom).orElse(null);
    }

    private ApiException introuvable() {
        return new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Billet introuvable.");
    }
}
