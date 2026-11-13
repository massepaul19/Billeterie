package cm.billetterie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ReservationRequete(
        @NotNull Long voyageId,
        @NotBlank String nom,
        @NotBlank String prenom,
        @NotBlank @Pattern(regexp = "^(\\+?237)?6\\d{8}$", message = "numéro camerounais invalide") String telephone,
        @NotBlank @Email String email) {}
