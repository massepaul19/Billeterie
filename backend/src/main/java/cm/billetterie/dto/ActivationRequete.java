package cm.billetterie.dto;

import jakarta.validation.constraints.NotNull;

public record ActivationRequete(@NotNull Boolean actif) {
}
