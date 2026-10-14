package cm.billetterie.payment;

import cm.billetterie.entity.enums.StatutPaiement;

public record PaiementResultat(String reference, StatutPaiement statut) {}
