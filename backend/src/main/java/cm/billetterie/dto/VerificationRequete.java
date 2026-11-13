package cm.billetterie.dto;

import jakarta.validation.constraints.NotBlank;

public record VerificationRequete(@NotBlank String code) {
}
