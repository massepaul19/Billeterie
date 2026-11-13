package cm.billetterie.dto;

import jakarta.validation.constraints.NotBlank;

public record ConnexionRequete(@NotBlank String telephone, @NotBlank String motDePasse) {
}
