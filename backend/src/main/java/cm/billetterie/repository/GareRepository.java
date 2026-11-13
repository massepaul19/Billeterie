package cm.billetterie.repository;

import cm.billetterie.entity.Gare;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GareRepository extends JpaRepository<Gare, Long> {
    List<Gare> findByAgenceIdOrderByNom(Long agenceId);

    Optional<Gare> findByIdAndAgenceId(Long id, Long agenceId);
}
