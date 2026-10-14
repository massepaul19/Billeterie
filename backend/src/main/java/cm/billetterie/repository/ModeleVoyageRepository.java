package cm.billetterie.repository;

import cm.billetterie.entity.*;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ModeleVoyageRepository extends JpaRepository<ModeleVoyage, Long> {
    // TODO : requêtes spécifiques à ModeleVoyage
}
