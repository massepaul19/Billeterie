package cm.billetterie.dto;

public record AgenceAdminReponse(Long id, String nom, String logo, String slogan, Long villeId, String villeNom,
                                 Double latitude, Double longitude, String numeroMobileMoney, boolean actif,
                                 String compteTelephone) {
}
