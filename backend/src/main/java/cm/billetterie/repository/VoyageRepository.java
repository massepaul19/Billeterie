package cm.billetterie.repository;

import cm.billetterie.entity.*;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface VoyageRepository extends JpaRepository<Voyage, Long> {

    /** RG3 : décompte atomique. Retourne 1 si une place a été prise, 0 sinon (plus de place). */
    @Modifying
    @Transactional
    @Query("UPDATE Voyage v SET v.placesRestantes = v.placesRestantes - 1 WHERE v.id = :id AND v.placesRestantes >= 1")
    int reserverUnePlace(@Param("id") Long id);

    /** Libère une place (expiration, annulation). */
    @Modifying
    @Transactional
    @Query("UPDATE Voyage v SET v.placesRestantes = v.placesRestantes + 1 WHERE v.id = :id")
    int libererUnePlace(@Param("id") Long id);

    // TODO (Back B) : requête de recherche publique (voir docs/API.md GET /api/voyages)
}
