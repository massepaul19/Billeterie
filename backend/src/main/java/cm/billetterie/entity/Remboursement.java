package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "remboursement")
@Getter @Setter @NoArgsConstructor
public class Remboursement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "montant")
    private Integer montant;

    @Column(name = "motif")
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutRemboursement statut;

    @Column(name = "cree_le")
    private LocalDateTime creeLe;

}
