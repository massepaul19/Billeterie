package cm.billetterie.repository;

import cm.billetterie.entity.ModeleVoyage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeleAgenceRepository extends JpaRepository<ModeleVoyage, Long> {
    List<ModeleVoyage> findByAgenceIdOrderById(Long agenceId);

    Optional<ModeleVoyage> findByIdAndAgenceId(Long id, Long agenceId);

    List<ModeleVoyage> findByActifTrue();
}
