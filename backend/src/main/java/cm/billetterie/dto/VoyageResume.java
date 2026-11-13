package cm.billetterie.dto;

import java.time.LocalDateTime;

/** Voyage tel que le voit le public. Jamais de places restantes. */
public record VoyageResume(
        Long id,
        AgenceInfo agence,
        LieuInfo depart,
        LieuInfo arrivee,
        LocalDateTime dateHeureDepart,
        Integer dureeEstimeeMin,
        Integer prix,
        VehiculeInfo vehicule) {

    public record AgenceInfo(Long id, String nom, String logo, String slogan) {}

    public record LieuInfo(String ville, String gare, String quartier) {}

    public record VehiculeInfo(String type, Boolean clim, Boolean wifi, String image) {}
}
