package cm.billetterie.repository;

import cm.billetterie.entity.Vehicule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    List<Vehicule> findByAgenceIdOrderByImmatriculation(Long agenceId);

    Optional<Vehicule> findByIdAndAgenceId(Long id, Long agenceId);

    Optional<Vehicule> findByAgenceIdAndImmatriculationIgnoreCase(Long agenceId, String immatriculation);
}
