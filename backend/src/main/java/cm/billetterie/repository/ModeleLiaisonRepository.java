package cm.billetterie.repository;

import cm.billetterie.entity.ModeleVoyage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeleLiaisonRepository extends JpaRepository<ModeleVoyage, Long> {
    boolean existsByVehiculeId(Long vehiculeId);

    boolean existsByTrajetId(Long trajetId);
}
