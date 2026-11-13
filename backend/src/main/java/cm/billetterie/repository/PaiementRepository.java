package cm.billetterie.repository;

import cm.billetterie.entity.*;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findFirstByReservationIdOrderByIdDesc(Long reservationId);
}
