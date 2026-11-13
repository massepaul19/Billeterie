package cm.billetterie.scheduler;

import cm.billetterie.service.AgenceModeleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ModeleScheduler {

    private static final Logger log = LoggerFactory.getLogger(ModeleScheduler.class);

    private final AgenceModeleService service;

    public ModeleScheduler(AgenceModeleService service) {
        this.service = service;
    }

    /** Toutes les 6 heures : génère les voyages manquants des modèles actifs. */
    @Scheduled(initialDelay = 60_000, fixedDelay = 6 * 3_600_000L)
    public void generer() {
        try {
            int nb = service.genererPourTous();
            if (nb > 0) log.info("{} voyage(s) généré(s) depuis les modèles", nb);
        } catch (RuntimeException e) {
            log.error("Échec de la génération des voyages", e);
        }
    }
}
