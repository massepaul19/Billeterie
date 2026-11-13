package cm.billetterie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompteAgenceRequete(@NotBlank String telephone, @Email String email,
                                  @NotBlank @Size(min = 6, message = "6 caractères minimum") String motDePasseInitial) {
}
