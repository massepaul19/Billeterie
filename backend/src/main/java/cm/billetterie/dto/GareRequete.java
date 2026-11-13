package cm.billetterie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GareRequete(@NotNull Long villeId, @NotBlank String nom, String quartier, String adresse,
                          Double latitude, Double longitude) {
}
