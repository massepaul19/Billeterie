package cm.billetterie.repository;

import cm.billetterie.entity.*;
import java.util.List;
import org.springframework.data.jpa.repository.*;

public interface VilleRepository extends JpaRepository<Ville, Long> {
    List<Ville> findByNomContainingIgnoreCaseOrderByNom(String q);
    List<Ville> findAllByOrderByNom();
}
