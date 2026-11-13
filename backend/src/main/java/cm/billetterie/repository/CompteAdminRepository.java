package cm.billetterie.repository;

import cm.billetterie.entity.*;
import cm.billetterie.entity.enums.RoleCompte;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CompteAdminRepository extends JpaRepository<CompteAdmin, Long> {
    Optional<CompteAdmin> findByTelephone(String telephone);

    List<CompteAdmin> findByAgenceId(Long agenceId);

    boolean existsByRole(RoleCompte role);

    boolean existsByTelephone(String telephone);
}
