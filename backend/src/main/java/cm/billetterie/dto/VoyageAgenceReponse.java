package cm.billetterie.dto;

import cm.billetterie.entity.enums.StatutVoyage;
import java.time.LocalDateTime;

public record VoyageAgenceReponse(Long id, Long trajetId, String gareDepart, String gareArrivee,
                                  Long vehiculeId, String immatriculation, Integer nbPlaces,
                                  LocalDateTime dateHeureDepart, Integer prix, Integer placesRestantes,
                                  StatutVoyage statut, Long modeleId) {
}
