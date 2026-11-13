package cm.billetterie.util;

import java.text.Normalizer;
import java.util.Locale;

public final class Texte {
    private Texte() {}

    /** Minuscules, sans accents, sans espaces superflus : pour comparer "Yaoundé" et "yaounde". */
    public static String normaliser(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    /** Garde les chiffres et retire l'indicatif 237 : "+237 677 00 00 00" -> "677000000". */
    public static String telephone(String s) {
        if (s == null) return "";
        String d = s.replaceAll("\\D", "");
        if (d.startsWith("237") && d.length() == 12) d = d.substring(3);
        return d;
    }
}
