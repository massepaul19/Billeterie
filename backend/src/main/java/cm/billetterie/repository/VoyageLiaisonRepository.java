package cm.billetterie.repository;

import cm.billetterie.entity.Voyage;
import org.springframework.data.jpa.repository.JpaRepository;

/** Contrôles d'usage avant suppression d'un véhicule ou d'un trajet. */
public interface VoyageLiaisonRepository extends JpaRepository<Voyage, Long> {
    boolean existsByVehiculeId(Long vehiculeId);

    boolean existsByTrajetId(Long trajetId);
}
