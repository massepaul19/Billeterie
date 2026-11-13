package cm.billetterie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgenceAdminRequete(@NotBlank String nom, String slogan, @NotNull Long villeId,
                                 Double latitude, Double longitude, String numeroMobileMoney) {
}
