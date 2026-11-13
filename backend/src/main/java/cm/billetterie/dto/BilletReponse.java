package cm.billetterie.dto;

import cm.billetterie.entity.enums.StatutBillet;
import java.time.LocalDateTime;

public record BilletReponse(String code, StatutBillet statut, LocalDateTime valideJusquA,
                            ClientInfo client, VoyageResume voyage) {

    public record ClientInfo(String nom, String prenom) {}
}
