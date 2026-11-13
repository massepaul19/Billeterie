package cm.billetterie.scheduler;

import cm.billetterie.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpirationReservationScheduler {

    private static final Logger log = LoggerFactory.getLogger(ExpirationReservationScheduler.class);

    private final ReservationService reservations;

    public ExpirationReservationScheduler(ReservationService reservations) {
        this.reservations = reservations;
    }

    /** RG4 : toutes les minutes, expire les réservations EN_ATTENTE dont expire_a est dépassé et libère la place. */
    @Scheduled(fixedDelay = 60_000)
    public void expirerLesReservations() {
        try {
            int nb = reservations.expirerLesReservations();
            if (nb > 0) log.info("{} réservation(s) expirée(s)", nb);
        } catch (RuntimeException e) {
            log.error("Échec de l'expiration des réservations", e);
        }
    }
}
