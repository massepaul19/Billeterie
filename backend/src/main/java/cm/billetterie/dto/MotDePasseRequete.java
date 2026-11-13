package cm.billetterie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MotDePasseRequete(@NotBlank String ancien,
                                @NotBlank @Size(min = 6, message = "6 caractères minimum") String nouveau) {
}
