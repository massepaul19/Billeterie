package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "paiement")
@Getter @Setter @NoArgsConstructor
public class Paiement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id")
    private Long reservationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "fournisseur")
    private FournisseurPaiement fournisseur;

    @Column(name = "reference")
    private String reference;

    @Column(name = "montant")
    private Integer montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutPaiement statut;

    @Column(name = "cree_le")
    private LocalDateTime creeLe;

}
