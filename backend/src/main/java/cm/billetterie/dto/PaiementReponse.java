package cm.billetterie.dto;

import cm.billetterie.entity.enums.StatutPaiement;
import cm.billetterie.entity.enums.StatutReservation;

public record PaiementReponse(Long id, StatutPaiement statut, String reference,
                              StatutReservation reservationStatut, String codeBillet) {}
