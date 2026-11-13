package cm.billetterie.dto;

import cm.billetterie.entity.enums.StatutBillet;
import java.time.LocalDateTime;

public record BilletRechercheReponse(String code, StatutBillet statut, String nom, String prenom,
                                     String telephone, Long voyageId, LocalDateTime dateHeureDepart) {
}
