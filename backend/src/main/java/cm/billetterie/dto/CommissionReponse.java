package cm.billetterie.dto;

import cm.billetterie.entity.enums.StatutFacturation;

public record CommissionReponse(Long agenceId, String agenceNom, Integer totalVentes, Integer commission,
                                StatutFacturation statutFacturation) {
}
