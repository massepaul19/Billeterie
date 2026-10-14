package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "reservation")
@Getter @Setter @NoArgsConstructor
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voyage_id")
    private Long voyageId;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "montant")
    private Integer montant;

    @Column(name = "commission")
    private Integer commission;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutReservation statut;

    @Column(name = "expire_a")
    private LocalDateTime expireA;

    @Column(name = "cree_le")
    private LocalDateTime creeLe;

    @Column(name = "ip_client")
    private String ipClient;

}
