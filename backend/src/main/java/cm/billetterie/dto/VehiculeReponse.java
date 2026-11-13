package cm.billetterie.dto;

import cm.billetterie.entity.enums.TypeVehicule;

public record VehiculeReponse(Long id, String immatriculation, TypeVehicule type, Integer nbPlaces,
                              boolean clim, boolean wifi, String image) {
}
