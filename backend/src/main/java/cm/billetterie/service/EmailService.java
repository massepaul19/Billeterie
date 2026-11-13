package cm.billetterie.service;

import cm.billetterie.entity.Billet;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> expediteurMail;
    private final BilletDonneesService donnees;
    private final BilletPdfService pdf;
    private final String adresseExpediteur;
    private final String urlPublique;

    public EmailService(ObjectProvider<JavaMailSender> expediteurMail, BilletDonneesService donnees,
                        BilletPdfService pdf,
                        @Value("${billetterie.mail.expediteur:no-reply@billetterie.local}") String adresseExpediteur,
                        @Value("${billetterie.public-url:http://localhost:6012}") String urlPublique) {
        this.expediteurMail = expediteurMail;
        this.donnees = donnees;
        this.pdf = pdf;
        this.adresseExpediteur = adresseExpediteur;
        this.urlPublique = urlPublique;
    }

    /** Vrai si un serveur SMTP est configuré (spring.mail.host). */
    public boolean disponible() {
        return expediteurMail.getIfAvailable() != null;
    }

    /** E-mail de confirmation avec le billet PDF en pièce jointe. */
    public boolean envoyerBillet(Billet billet) {
        if (!disponible()) {
            log.info("SMTP non configuré : e-mail du billet non envoyé.");
            return false;
        }
        BilletDonnees d = donnees.charger(billet);
        String texte = "Bonjour " + d.nomComplet() + ",\n\n"
                + "Votre paiement est confirmé. Votre billet est en pièce jointe.\n\n"
                + resume(d)
                + "\nVous pouvez aussi le télécharger ici : " + lien(d) + "\n\n"
                + "Bon voyage !\n" + d.agence();
        return envoyer(d.email(), "Votre billet " + d.agence() + " : " + d.villeDepart() + " > " + d.villeArrivee(),
                texte, pdf.generer(d), "billet-" + d.code() + ".pdf");
    }

    /** E-mail de rappel avant le départ. */
    public boolean envoyerRappel(Billet billet, BilletDonnees d) {
        if (!disponible()) return false;
        String texte = "Bonjour " + d.nomComplet() + ",\n\n"
                + "Rappel : votre voyage approche.\n\n"
                + resume(d)
                + "\nVotre billet : " + lien(d) + "\n\n"
                + "Soyez à la gare un peu avant l'heure de départ.\n" + d.agence();
        return envoyer(d.email(), "Rappel de voyage " + d.agence() + " : " + d.villeDepart() + " > " + d.villeArrivee(),
                texte, pdf.generer(d), "billet-" + d.code() + ".pdf");
    }

    private String resume(BilletDonnees d) {
        return "Départ : " + d.lieuDepart() + "\n"
                + "Arrivée : " + d.lieuArrivee() + "\n"
                + "Date : " + d.departFormate() + "\n"
                + "Code du billet : " + d.code() + "\n";
    }

    private String lien(BilletDonnees d) {
        return urlPublique + "/api/billets/" + d.code() + "/pdf";
    }

    private boolean envoyer(String destinataire, String sujet, String texte, byte[] piece, String nomPiece) {
        if (destinataire == null || destinataire.isBlank()) return false;
        JavaMailSender sender = expediteurMail.getIfAvailable();
        if (sender == null) return false;
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(adresseExpediteur);
            helper.setTo(destinataire);
            helper.setSubject(sujet);
            helper.setText(texte);
            helper.addAttachment(nomPiece, new ByteArrayResource(piece), "application/pdf");
            sender.send(message);
            return true;
        } catch (Exception e) {
            log.error("Échec de l'envoi de l'e-mail à {}", destinataire, e);
            return false;
        }
    }
}
