package cm.billetterie.repository;

import cm.billetterie.entity.Reservation;
import cm.billetterie.entity.enums.StatutReservation;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationAgenceRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByVoyageId(Long voyageId);

    List<Reservation> findByClientIdIn(Collection<Long> clientIds);

    @Query("select r from Reservation r where r.voyageId in "
            + "(select v.id from Voyage v where v.agenceId = :agenceId) order by r.creeLe desc")
    List<Reservation> findParAgence(@Param("agenceId") Long agenceId);

    /** [agenceId, somme des montants, somme des commissions] des réservations d'un statut sur une période. */
    @Query("select v.agenceId, sum(r.montant), sum(r.commission) from Reservation r, Voyage v "
            + "where r.voyageId = v.id and r.statut = :statut and r.creeLe >= :debut and r.creeLe < :fin "
            + "group by v.agenceId")
    List<Object[]> totauxParAgence(@Param("statut") StatutReservation statut,
                                   @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
