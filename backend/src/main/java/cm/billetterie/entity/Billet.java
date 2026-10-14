package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "billet")
@Getter @Setter @NoArgsConstructor
public class Billet {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "code_unique")
    private String codeUnique;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutBillet statut;

    @Column(name = "valide_jusqu_a")
    private LocalDateTime valideJusquA;

    @Column(name = "reutilise")
    private Boolean reutilise = false;

}
