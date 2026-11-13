package cm.billetterie.repository;

import cm.billetterie.entity.*;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;

public interface BilletRepository extends JpaRepository<Billet, Long> {
    Optional<Billet> findByCodeUnique(String codeUnique);
    Optional<Billet> findByReservationId(Long reservationId);
}
