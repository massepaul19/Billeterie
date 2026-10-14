package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "releve_commission")
@Getter @Setter @NoArgsConstructor
public class ReleveCommission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agence_id")
    private Long agenceId;

    @Column(name = "mois")
    private String mois;

    @Column(name = "total_ventes")
    private Integer totalVentes;

    @Column(name = "commission")
    private Integer commission;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_facturation")
    private StatutFacturation statutFacturation;

}
