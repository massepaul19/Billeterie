package cm.billetterie.dto;

import cm.billetterie.entity.enums.StatutRemboursement;

public record AnnulationReponse(RemboursementInfo remboursement) {

    public record RemboursementInfo(Integer montant, StatutRemboursement statut) {}
}
