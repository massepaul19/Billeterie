package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "rappel")
@Getter @Setter @NoArgsConstructor
public class Rappel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "billet_id")
    private Long billetId;

    @Column(name = "envoyer_a")
    private LocalDateTime envoyerA;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal")
    private CanalRappel canal;

    @Column(name = "envoye")
    private Boolean envoye = false;

}
