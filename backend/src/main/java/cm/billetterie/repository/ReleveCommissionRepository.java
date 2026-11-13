package cm.billetterie.repository;

import cm.billetterie.entity.*;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;

public interface ReleveCommissionRepository extends JpaRepository<ReleveCommission, Long> {
    List<ReleveCommission> findByMois(String mois);

    Optional<ReleveCommission> findByAgenceIdAndMois(Long agenceId, String mois);
}
