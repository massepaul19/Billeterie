package cm.billetterie.dto;

public record GareReponse(Long id, Long villeId, String villeNom, String nom, String quartier, String adresse,
                          Double latitude, Double longitude) {
}
