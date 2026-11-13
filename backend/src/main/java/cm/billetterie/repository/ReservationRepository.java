package cm.billetterie.repository;

import cm.billetterie.entity.*;
import cm.billetterie.entity.enums.StatutReservation;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatut(StatutReservation statut);

    List<Reservation> findByIpClientAndStatut(String ipClient, StatutReservation statut);

    @Query("SELECT r FROM Reservation r, Client c WHERE r.clientId = c.id AND c.telephone = :telephone AND r.statut = :statut")
    List<Reservation> findParTelephoneEtStatut(@Param("telephone") String telephone,
                                               @Param("statut") StatutReservation statut);
}
