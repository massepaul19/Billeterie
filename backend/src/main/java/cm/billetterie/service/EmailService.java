package cm.billetterie.service;

import org.springframework.stereotype.Service;

/** Envoi d'emails via Gmail (SMTP). Plus de PWA ni de push : l'email est le seul canal de rappel. */
@Service
public class EmailService {

    // TODO (Back A, Sprint 2) : configurer spring.mail.* dans application.properties
    //   (mot de passe d'application Gmail, jamais dans Git : variable d'environnement)
    //   puis injecter JavaMailSender et coder envoyerRappel(...) et envoyerBillet(...)
}
