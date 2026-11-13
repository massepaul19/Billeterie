package cm.billetterie.repository;

import cm.billetterie.entity.Trajet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {
    List<Trajet> findByAgenceIdOrderById(Long agenceId);

    Optional<Trajet> findByIdAndAgenceId(Long id, Long agenceId);

    boolean existsByGareDepartIdOrGareArriveeId(Long gareDepartId, Long gareArriveeId);
}
