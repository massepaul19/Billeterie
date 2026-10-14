package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "agence")
@Getter @Setter @NoArgsConstructor
public class Agence {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "logo")
    private String logo;

    @Column(name = "slogan")
    private String slogan;

    @Column(name = "ville_id")
    private Long villeId;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "numero_mobile_money")
    private String numeroMobileMoney;

    @Column(name = "devise")
    private String devise;

    @Column(name = "delais_rappel_heures")
    private String delaisRappelHeures;

    @Column(name = "actif")
    private Boolean actif = true;

}
