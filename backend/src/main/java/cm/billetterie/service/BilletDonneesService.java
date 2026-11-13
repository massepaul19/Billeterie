package cm.billetterie.service;

import cm.billetterie.entity.Billet;
import cm.billetterie.entity.Client;
import cm.billetterie.entity.Reservation;
import cm.billetterie.entity.Voyage;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.ClientRepository;
import cm.billetterie.repository.ReservationRepository;
import cm.billetterie.repository.VoyageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BilletDonneesService {

    private final ReservationRepository reservations;
    private final ClientRepository clients;
    private final VoyageRepository voyages;
    private final VoyageAssembleur assembleur;

    public BilletDonneesService(ReservationRepository reservations, ClientRepository clients,
                                VoyageRepository voyages, VoyageAssembleur assembleur) {
        this.reservations = reservations;
        this.clients = clients;
        this.voyages = voyages;
        this.assembleur = assembleur;
    }

    public BilletDonnees charger(Billet billet) {
        Reservation res = reservations.findById(billet.getReservationId()).orElseThrow(this::introuvable);
        Client client = clients.findById(res.getClientId()).orElseThrow(this::introuvable);
        Voyage voyage = voyages.findById(res.getVoyageId()).orElseThrow(this::introuvable);
        var d = assembleur.chargerUn(voyage).orElseThrow(this::introuvable);

        long montant = res.getMontant();
        return new BilletDonnees(
                billet.getCodeUnique(), billet.getStatut(), billet.getValideJusquA(),
                d.agence().getNom(), d.agence().getSlogan(),
                client.getNom(), client.getPrenom(), client.getEmail(), client.getTelephone(),
                d.villeDepart().getNom(), d.gareDepart().getNom(), d.gareDepart().getQuartier(),
                d.villeArrivee().getNom(), d.gareArrivee().getNom(), d.gareArrivee().getQuartier(),
                voyage.getDateHeureDepart(), montant,
                d.vehicule().getType() == null ? "" : d.vehicule().getType().name(),
                Boolean.TRUE.equals(d.vehicule().getClim()), Boolean.TRUE.equals(d.vehicule().getWifi()));
    }

    private ApiException introuvable() {
        return new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Données du billet introuvables.");
    }
}
