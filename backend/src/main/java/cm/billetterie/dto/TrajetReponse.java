package cm.billetterie.dto;

public record TrajetReponse(Long id, Long gareDepartId, String gareDepartNom, Long gareArriveeId,
                            String gareArriveeNom, Integer dureeEstimeeMin) {
}
