package cm.billetterie.repository;

import cm.billetterie.entity.Rappel;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RappelEnvoiRepository extends JpaRepository<Rappel, Long> {

    @Query("select r from Rappel r where r.envoye = false and r.envoyerA <= :maintenant order by r.envoyerA")
    List<Rappel> findDus(@Param("maintenant") LocalDateTime maintenant);

    @Modifying
    @Transactional
    @Query("update Rappel r set r.envoye = true where r.id = :id")
    int marquerEnvoye(@Param("id") Long id);
}
