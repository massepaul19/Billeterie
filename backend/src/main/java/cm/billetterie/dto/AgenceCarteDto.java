package cm.billetterie.dto;

/** Agence affichée sur la carte publique. */
public record AgenceCarteDto(Long id, String nom, String logo, String slogan, String ville,
                             Double latitude, Double longitude) {}
