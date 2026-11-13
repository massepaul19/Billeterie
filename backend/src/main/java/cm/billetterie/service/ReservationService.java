package cm.billetterie.service;

import cm.billetterie.dto.ReservationReponse;
import cm.billetterie.dto.ReservationRequete;
import cm.billetterie.entity.Client;
import cm.billetterie.entity.Reservation;
import cm.billetterie.entity.Voyage;
import cm.billetterie.entity.enums.StatutReservation;
import cm.billetterie.entity.enums.StatutVoyage;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.AgenceRepository;
import cm.billetterie.repository.ClientRepository;
import cm.billetterie.repository.ReservationRepository;
import cm.billetterie.repository.VoyageRepository;
import cm.billetterie.util.Texte;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private static final ZoneId ZONE = ZoneId.of("Africa/Douala");

    private final VoyageRepository voyages;
    private final AgenceRepository agences;
    private final ClientRepository clients;
    private final ReservationRepository reservations;
    private final int dureeBlocageMinutes;
    private final int maxEnAttente;

    public ReservationService(VoyageRepository voyages, AgenceRepository agences, ClientRepository clients,
                              ReservationRepository reservations,
                              @Value("${billetterie.reservation.duree-blocage-minutes}") int dureeBlocageMinutes,
                              @Value("${billetterie.reservation.max-en-attente-par-telephone}") int maxEnAttente) {
        this.voyages = voyages;
        this.agences = agences;
        this.clients = clients;
        this.reservations = reservations;
        this.dureeBlocageMinutes = dureeBlocageMinutes;
        this.maxEnAttente = maxEnAttente;
    }

    @Transactional
    public ReservationReponse creer(ReservationRequete r, String ip) {
        LocalDateTime maintenant = LocalDateTime.now(ZONE);

        Voyage voyage = voyages.findById(r.voyageId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Voyage introuvable."));
        boolean agenceActive = agences.findById(voyage.getAgenceId())
                .map(a -> Boolean.TRUE.equals(a.getActif())).orElse(false);
        if (!agenceActive || voyage.getStatut() != StatutVoyage.PROGRAMME
                || voyage.getDateHeureDepart() == null || !voyage.getDateHeureDepart().isAfter(maintenant)) {
            throw new ApiException(HttpStatus.CONFLICT, "PLACES_INDISPONIBLES", "Ce voyage n'est plus disponible.");
        }

        // RG19 : maximum de réservations en attente par téléphone et par IP
        String telephone = Texte.telephone(r.telephone());
        long parTelephone = enAttenteValides(
                reservations.findParTelephoneEtStatut(telephone, StatutReservation.EN_ATTENTE), maintenant);
        long parIp = (ip == null || ip.isBlank()) ? 0
                : enAttenteValides(reservations.findByIpClientAndStatut(ip, StatutReservation.EN_ATTENTE), maintenant);
        if (parTelephone >= maxEnAttente || parIp >= maxEnAttente) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "TROP_DE_RESERVATIONS",
                    "Trop de réservations en attente. Terminez ou attendez l'expiration des précédentes.");
        }

        // RG3 : décompte atomique de la place
        if (voyages.reserverUnePlace(voyage.getId()) == 0) {
            throw new ApiException(HttpStatus.CONFLICT, "PLACES_INDISPONIBLES",
                    "Il n'y a plus de place pour ce voyage.");
        }

        Client client = new Client();
        client.setNom(r.nom().trim());
        client.setPrenom(r.prenom().trim());
        client.setTelephone(telephone);
        client.setEmail(r.email().trim());
        client = clients.save(client);

        Reservation res = new Reservation();
        res.setVoyageId(voyage.getId());
        res.setClientId(client.getId());
        res.setMontant(voyage.getPrix());
        res.setCommission(0);
        res.setStatut(StatutReservation.EN_ATTENTE);
        res.setCreeLe(maintenant);
        res.setExpireA(maintenant.plusMinutes(dureeBlocageMinutes));
        res.setIpClient(ip);
        res = reservations.save(res);
        return versReponse(res);
    }

    /** Statut à jour : une réservation EN_ATTENTE dont le délai est dépassé passe à EXPIREE. */
    @Transactional
    public ReservationReponse obtenir(Long id) {
        Reservation res = reservations.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Réservation introuvable."));
        if (res.getStatut() == StatutReservation.EN_ATTENTE && res.getExpireA() != null
                && res.getExpireA().isBefore(LocalDateTime.now(ZONE))) {
            expirer(res);
        }
        return versReponse(res);
    }

    /** RG4 : passe la réservation à EXPIREE et libère la place. */
    @Transactional
    public void expirer(Reservation res) {
        res.setStatut(StatutReservation.EXPIREE);
        reservations.save(res);
        voyages.libererUnePlace(res.getVoyageId());
    }

    @Transactional
    public int expirerLesReservations() {
        LocalDateTime maintenant = LocalDateTime.now(ZONE);
        int nb = 0;
        for (Reservation r : reservations.findByStatut(StatutReservation.EN_ATTENTE)) {
            if (r.getExpireA() != null && r.getExpireA().isBefore(maintenant)) {
                expirer(r);
                nb++;
            }
        }
        return nb;
    }

    private static long enAttenteValides(List<Reservation> liste, LocalDateTime maintenant) {
        return liste.stream().filter(x -> x.getExpireA() != null && x.getExpireA().isAfter(maintenant)).count();
    }

    private static ReservationReponse versReponse(Reservation r) {
        return new ReservationReponse(r.getId(), r.getVoyageId(), r.getStatut(), r.getMontant(), r.getExpireA());
    }
}
