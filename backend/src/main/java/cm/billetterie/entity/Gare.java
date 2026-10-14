package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "gare")
@Getter @Setter @NoArgsConstructor
public class Gare {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agence_id")
    private Long agenceId;

    @Column(name = "ville_id")
    private Long villeId;

    @Column(name = "nom")
    private String nom;

    @Column(name = "quartier")
    private String quartier;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

}
