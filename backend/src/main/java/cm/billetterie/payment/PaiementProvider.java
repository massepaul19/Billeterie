package cm.billetterie.payment;

import cm.billetterie.entity.Reservation;

/** Interface qui isole le reste du système du moyen de paiement (cahier des charges, M6). */
public interface PaiementProvider {
    PaiementResultat payer(Reservation reservation, String telephonePaiement, String operateur);
}
