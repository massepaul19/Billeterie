package cm.billetterie.scheduler;

import cm.billetterie.service.RappelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RappelScheduler {

    private static final Logger log = LoggerFactory.getLogger(RappelScheduler.class);

    private final RappelService rappels;

    public RappelScheduler(RappelService rappels) {
        this.rappels = rappels;
    }

    @Scheduled(initialDelay = 30_000, fixedDelay = 60_000)
    public void envoyerLesRappels() {
        try {
            int nb = rappels.envoyerRappelsDus();
            if (nb > 0) log.info("{} rappel(s) envoyé(s)", nb);
        } catch (RuntimeException e) {
            log.error("Échec de l'envoi des rappels", e);
        }
    }
}
