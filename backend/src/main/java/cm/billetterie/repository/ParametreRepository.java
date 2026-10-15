package cm.billetterie.repository;

import cm.billetterie.entity.Parametre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParametreRepository extends JpaRepository<Parametre, String> {
}
