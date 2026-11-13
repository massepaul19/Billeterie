package cm.billetterie.service;

import cm.billetterie.entity.enums.StatutBillet;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Toutes les informations d'un billet, prêtes pour le PDF et les e-mails. */
public record BilletDonnees(
        String code, StatutBillet statut, LocalDateTime valideJusquA,
        String agence, String slogan,
        String nom, String prenom, String email, String telephone,
        String villeDepart, String gareDepart, String quartierDepart,
        String villeArrivee, String gareArrivee, String quartierArrivee,
        LocalDateTime depart, long montant, String vehicule, boolean clim, boolean wifi) {

    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("EEEE d MMMM yyyy 'à' HH'h'mm", Locale.FRENCH);

    public String nomComplet() {
        return (prenom == null ? "" : prenom) + " " + (nom == null ? "" : nom);
    }

    public String lieuDepart() {
        return lieu(villeDepart, gareDepart, quartierDepart);
    }

    public String lieuArrivee() {
        return lieu(villeArrivee, gareArrivee, quartierArrivee);
    }

    public String departFormate() {
        return depart == null ? "" : FORMAT_DATE.format(depart);
    }

    public String montantFormate() {
        // espace simple (le séparateur français est une espace insécable que la police PDF ne gère pas)
        return NumberFormat.getIntegerInstance(Locale.ROOT).format(montant).replace(',', ' ') + " FCFA";
    }

    public String equipements() {
        List<String> l = new ArrayList<>();
        if (vehicule != null && !vehicule.isBlank()) l.add(vehicule);
        if (clim) l.add("climatisation");
        if (wifi) l.add("wifi");
        return String.join(", ", l);
    }

    private static String lieu(String ville, String gare, String quartier) {
        StringBuilder sb = new StringBuilder(ville == null ? "" : ville);
        if (gare != null && !gare.isBlank()) sb.append(" - ").append(gare);
        if (quartier != null && !quartier.isBlank()) sb.append(" (").append(quartier).append(")");
        return sb.toString();
    }
}
