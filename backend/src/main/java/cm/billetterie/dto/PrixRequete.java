package cm.billetterie.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PrixRequete(@NotNull @Min(0) Integer prix) {
}
