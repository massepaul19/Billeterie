package cm.billetterie.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/** Format de pagination de l'API : { contenu, page, taille, total, derniere }. */
public record PageReponse<T>(List<T> contenu, int page, int taille, long total, boolean derniere) {

    public static <T> PageReponse<T> depuis(Page<T> p) {
        return new PageReponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.isLast());
    }

    /** Pagination en mémoire (utile quand le tri se fait en Java). */
    public static <T> PageReponse<T> depuisListe(List<T> tout, int page, int taille) {
        int debut = Math.min(page * taille, tout.size());
        int fin = Math.min(debut + taille, tout.size());
        return new PageReponse<>(tout.subList(debut, fin), page, taille, tout.size(), fin >= tout.size());
    }
}
