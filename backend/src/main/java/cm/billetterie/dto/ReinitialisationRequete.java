package cm.billetterie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReinitialisationRequete(@NotBlank @Size(min = 6, message = "6 caractères minimum") String nouveauMotDePasse) {
}
