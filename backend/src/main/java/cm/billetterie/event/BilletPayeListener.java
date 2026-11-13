package cm.billetterie.event;

import cm.billetterie.repository.BilletRepository;
import cm.billetterie.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BilletPayeListener {

    private static final Logger log = LoggerFactory.getLogger(BilletPayeListener.class);

    private final BilletRepository billets;
    private final EmailService emails;

    public BilletPayeListener(BilletRepository billets, EmailService emails) {
        this.billets = billets;
        this.emails = emails;
    }

    /** Après validation de la transaction de paiement, envoie le billet par e-mail (sans bloquer la réponse). */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void surBilletPaye(BilletPayeEvent evenement) {
        try {
            billets.findById(evenement.billetId()).ifPresent(emails::envoyerBillet);
        } catch (RuntimeException e) {
            log.error("Échec de l'envoi du billet {}", evenement.billetId(), e);
        }
    }
}
