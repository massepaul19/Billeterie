package cm.billetterie.repository;

import cm.billetterie.entity.Voyage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoyageAgenceRepository extends JpaRepository<Voyage, Long> {
    List<Voyage> findByAgenceIdOrderByDateHeureDepartDesc(Long agenceId);

    Optional<Voyage> findByIdAndAgenceId(Long id, Long agenceId);

    List<Voyage> findByModeleId(Long modeleId);

    boolean existsByModeleIdAndDateHeureDepart(Long modeleId, LocalDateTime dateHeureDepart);

    boolean existsByAgenceId(Long agenceId);
}
