package cm.billetterie.repository;

import cm.billetterie.entity.Remboursement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RemboursementAgenceRepository extends JpaRepository<Remboursement, Long> {

    @Query("select r from Remboursement r where r.reservationId in (select x.id from Reservation x where x.voyageId in "
            + "(select v.id from Voyage v where v.agenceId = :agenceId)) order by r.creeLe desc")
    List<Remboursement> findParAgence(@Param("agenceId") Long agenceId);

    @Query("select r from Remboursement r where r.id = :id and r.reservationId in (select x.id from Reservation x "
            + "where x.voyageId in (select v.id from Voyage v where v.agenceId = :agenceId))")
    Optional<Remboursement> findByIdEtAgence(@Param("id") Long id, @Param("agenceId") Long agenceId);
}
