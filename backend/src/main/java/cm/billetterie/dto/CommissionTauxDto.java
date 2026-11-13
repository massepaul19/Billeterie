package cm.billetterie.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/** Taux de commission (ex. 0.007 = 0,7 %). Sert en entrée et en sortie. */
public record CommissionTauxDto(@NotNull @DecimalMin("0.0") @DecimalMax("1.0") Double taux) {}
