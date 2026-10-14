package cm.billetterie.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpirationReservationScheduler {

    /** RG4 : toutes les minutes, passer à EXPIREE les réservations EN_ATTENTE dont expire_a est dépassé
     *  et libérer la place (VoyageRepository.libererUnePlace). */
    @Scheduled(fixedDelay = 60_000)
    public void expirerLesReservations() {
        // TODO (Back B, Sprint 2)
    }
}
