package cm.billetterie.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RappelScheduler {

    /** Envoie par email (Gmail) les rappels dont envoyer_a est dépassé et envoye = false. */
    @Scheduled(fixedDelay = 60_000)
    public void envoyerLesRappels() {
        // TODO (Back A, Sprint 2) : utiliser EmailService
    }
}
