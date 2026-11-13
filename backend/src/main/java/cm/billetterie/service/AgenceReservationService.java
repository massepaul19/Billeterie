package cm.billetterie.service;

import cm.billetterie.dto.PageReponse;
import cm.billetterie.dto.ReservationAgenceReponse;
import cm.billetterie.entity.*;
import cm.billetterie.entity.enums.StatutReservation;
import cm.billetterie.repository.BilletRepository;
import cm.billetterie.repository.ClientRepository;
import cm.billetterie.repository.ReservationAgenceRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AgenceReservationService {

    private final ReservationAgenceRepository reservations;
    private final ClientRepository clients;
    private final BilletRepository billets;

    public AgenceReservationService(ReservationAgenceRepository reservations, ClientRepository clients,
                                    BilletRepository billets) {
        this.reservations = reservations;
        this.clients = clients;
        this.billets = billets;
    }

    public PageReponse<ReservationAgenceReponse> lister(Long agenceId, StatutReservation statut,
                                                        Long voyageId, int page, int taille) {
        int p = Math.max(page, 0);
        int t = Math.min(Math.max(taille, 1), 100);
        List<Reservation> liste = reservations.findParAgence(agenceId).stream()
                .filter(r -> statut == null || r.getStatut() == statut)
                .filter(r -> voyageId == null || voyageId.equals(r.getVoyageId()))
                .toList();
        PageReponse<Reservation> brut = PageReponse.depuisListe(liste, p, t);
        return new PageReponse<>(brut.contenu().stream().map(this::vers).toList(),
                brut.page(), brut.taille(), brut.total(), brut.derniere());
    }

    private ReservationAgenceReponse vers(Reservation r) {
        Client c = r.getClientId() == null ? null : clients.findById(r.getClientId()).orElse(null);
        Billet b = billets.findByReservationId(r.getId()).orElse(null);
        return new ReservationAgenceReponse(r.getId(), r.getVoyageId(), r.getStatut(), r.getMontant(), r.getCreeLe(),
                c == null ? null : new ReservationAgenceReponse.ClientInfo(c.getNom(), c.getPrenom(),
                        c.getTelephone(), c.getEmail()),
                b == null ? null : new ReservationAgenceReponse.BilletInfo(b.getCodeUnique(), b.getStatut()));
    }
}
