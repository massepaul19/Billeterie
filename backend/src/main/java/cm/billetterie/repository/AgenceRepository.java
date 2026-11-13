package cm.billetterie.repository;

import cm.billetterie.entity.*;
import java.util.List;
import org.springframework.data.jpa.repository.*;

public interface AgenceRepository extends JpaRepository<Agence, Long> {
    List<Agence> findByActifTrueOrderByNom();
}
