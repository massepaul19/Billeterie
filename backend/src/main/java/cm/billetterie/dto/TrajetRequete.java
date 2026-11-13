package cm.billetterie.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TrajetRequete(@NotNull Long gareDepartId, @NotNull Long gareArriveeId,
                            @NotNull @Min(1) Integer dureeEstimeeMin) {
}
