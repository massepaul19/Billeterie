package cm.billetterie.dto;

import cm.billetterie.entity.enums.StatutRemboursement;
import java.time.LocalDateTime;

public record RemboursementReponse(Long id, Long reservationId, Integer montant, String motif,
                                   StatutRemboursement statut, LocalDateTime creeLe,
                                   String clientNom, String clientTelephone) {
}
