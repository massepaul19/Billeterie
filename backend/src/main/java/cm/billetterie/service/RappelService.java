package cm.billetterie.service;

import cm.billetterie.entity.Billet;
import cm.billetterie.entity.Rappel;
import cm.billetterie.entity.enums.StatutBillet;
import cm.billetterie.repository.BilletRepository;
import cm.billetterie.repository.RappelEnvoiRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RappelService {

    private static final Logger log = LoggerFactory.getLogger(RappelService.class);
    private static final ZoneId ZONE = ZoneId.of("Africa/Douala");
    /** Un rappel en retard de plus de 3 h n'a plus d'intérêt : on le clôt sans l'envoyer. */
    private static final long RETARD_MAX_HEURES = 3;

    private final RappelEnvoiRepository rappels;
    private final BilletRepository billets;
    private final BilletDonneesService donnees;
    private final EmailService emails;

    public RappelService(RappelEnvoiRepository rappels, BilletRepository billets,
                         BilletDonneesService donnees, EmailService emails) {
        this.rappels = rappels;
        this.billets = billets;
        this.donnees = donnees;
        this.emails = emails;
    }

    /** Envoie les rappels arrivés à échéance ; retourne le nombre d'e-mails envoyés. */
    public int envoyerRappelsDus() {
        if (!emails.disponible()) return 0;
        LocalDateTime maintenant = LocalDateTime.now(ZONE);
        int envoyes = 0;
        for (Rappel r : rappels.findDus(maintenant)) {
            try {
                if (traiter(r, maintenant)) envoyes++;
            } catch (RuntimeException e) {
                log.error("Échec du rappel {}", r.getId(), e);
            }
        }
        return envoyes;
    }

    private boolean traiter(Rappel r, LocalDateTime maintenant) {
        Billet billet = billets.findById(r.getBilletId()).orElse(null);
        if (billet == null || billet.getStatut() != StatutBillet.VALIDE
                || r.getEnvoyerA().isBefore(maintenant.minusHours(RETARD_MAX_HEURES))) {
            rappels.marquerEnvoye(r.getId());
            return false;
        }
        BilletDonnees d = donnees.charger(billet);
        if (d.depart() != null && !d.depart().isAfter(maintenant)) {
            rappels.marquerEnvoye(r.getId());
            return false;
        }
        if (emails.envoyerRappel(billet, d)) {
            rappels.marquerEnvoye(r.getId());
            return true;
        }
        return false; // nouvel essai à la minute suivante
    }
}
