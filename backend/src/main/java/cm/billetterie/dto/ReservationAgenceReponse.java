package cm.billetterie.dto;

import cm.billetterie.entity.enums.StatutBillet;
import cm.billetterie.entity.enums.StatutReservation;
import java.time.LocalDateTime;

public record ReservationAgenceReponse(Long id, Long voyageId, StatutReservation statut, Integer montant,
                                       LocalDateTime creeLe, ClientInfo client, BilletInfo billet) {

    public record ClientInfo(String nom, String prenom, String telephone, String email) {}

    public record BilletInfo(String code, StatutBillet statut) {}
}
