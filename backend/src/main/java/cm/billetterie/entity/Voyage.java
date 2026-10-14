package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "voyage")
@Getter @Setter @NoArgsConstructor
public class Voyage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agence_id")
    private Long agenceId;

    @Column(name = "trajet_id")
    private Long trajetId;

    @Column(name = "vehicule_id")
    private Long vehiculeId;

    @Column(name = "modele_id")
    private Long modeleId;

    @Column(name = "date_heure_depart")
    private LocalDateTime dateHeureDepart;

    @Column(name = "prix")
    private Integer prix;

    @Column(name = "places_restantes")
    private Integer placesRestantes;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutVoyage statut;

}
