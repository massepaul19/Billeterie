package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "vehicule")
@Getter @Setter @NoArgsConstructor
public class Vehicule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agence_id")
    private Long agenceId;

    @Column(name = "immatriculation")
    private String immatriculation;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeVehicule type;

    @Column(name = "nb_places")
    private Integer nbPlaces;

    @Column(name = "clim")
    private Boolean clim = false;

    @Column(name = "wifi")
    private Boolean wifi = false;

    @Column(name = "image")
    private String image;

}
