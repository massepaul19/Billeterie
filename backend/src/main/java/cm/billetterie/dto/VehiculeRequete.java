package cm.billetterie.dto;

import cm.billetterie.entity.enums.TypeVehicule;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehiculeRequete(@NotBlank String immatriculation, @NotNull TypeVehicule type,
                              @NotNull @Min(1) Integer nbPlaces, Boolean clim, Boolean wifi) {
}
