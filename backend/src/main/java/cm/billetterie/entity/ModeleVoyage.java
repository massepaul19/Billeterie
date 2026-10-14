package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "modele_voyage")
@Getter @Setter @NoArgsConstructor
public class ModeleVoyage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agence_id")
    private Long agenceId;

    @Column(name = "trajet_id")
    private Long trajetId;

    @Column(name = "vehicule_id")
    private Long vehiculeId;

    @Column(name = "prix")
    private Integer prix;

    @Column(name = "jours_semaine")
    private String joursSemaine;

    @Column(name = "heure")
    private String heure;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "actif")
    private Boolean actif = true;

}
