package cm.billetterie.dto;

import cm.billetterie.entity.enums.StatutReservation;
import java.time.LocalDateTime;

public record ReservationReponse(Long id, Long voyageId, StatutReservation statut, Integer montant,
                                 LocalDateTime expireA) {}
