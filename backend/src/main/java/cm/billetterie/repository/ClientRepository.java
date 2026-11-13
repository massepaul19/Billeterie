package cm.billetterie.repository;

import cm.billetterie.entity.Client;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("select c from Client c where lower(c.nom) like lower(concat('%', :q, '%')) "
            + "or lower(c.prenom) like lower(concat('%', :q, '%')) "
            + "or c.telephone like concat('%', :q, '%')")
    List<Client> rechercher(@Param("q") String q);
}
