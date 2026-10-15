package cm.billetterie.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

/** Paramètre modifiable. Ex. clé "commission_taux" = "0.007" (0,7 %). */
@Entity
@Table(name = "parametre")
@Getter @Setter @NoArgsConstructor
public class Parametre {

    @Id
    @Column(name = "cle")
    private String cle;

    @Column(name = "valeur")
    private String valeur;

    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;
}
