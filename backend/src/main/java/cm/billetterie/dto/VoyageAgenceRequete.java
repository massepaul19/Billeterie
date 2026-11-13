package cm.billetterie.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record VoyageAgenceRequete(@NotNull Long trajetId, @NotNull Long vehiculeId,
                                  @NotNull LocalDateTime dateHeureDepart, @NotNull @Min(0) Integer prix) {
}
