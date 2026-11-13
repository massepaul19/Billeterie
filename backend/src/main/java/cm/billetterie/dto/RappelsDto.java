package cm.billetterie.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record RappelsDto(@NotNull @Size(max = 5) List<@NotNull @Min(1) @Max(720) Integer> delaisHeures) {
}
