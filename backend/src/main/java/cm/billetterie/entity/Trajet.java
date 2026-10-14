package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "trajet")
@Getter @Setter @NoArgsConstructor
public class Trajet {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agence_id")
    private Long agenceId;

    @Column(name = "gare_depart_id")
    private Long gareDepartId;

    @Column(name = "gare_arrivee_id")
    private Long gareArriveeId;

    @Column(name = "duree_estimee_min")
    private Integer dureeEstimeeMin;

}
