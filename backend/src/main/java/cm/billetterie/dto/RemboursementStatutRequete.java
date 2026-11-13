package cm.billetterie.dto;

import cm.billetterie.entity.enums.StatutRemboursement;
import jakarta.validation.constraints.NotNull;

public record RemboursementStatutRequete(@NotNull StatutRemboursement statut) {
}
