package cm.billetterie.repository;

import cm.billetterie.entity.Remboursement;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemboursementRepository extends JpaRepository<Remboursement, Long> {
    List<Remboursement> findByReservationIdInOrderByCreeLeDesc(Collection<Long> reservationIds);
}
