package cm.billetterie.payment;

import cm.billetterie.entity.Reservation;
import org.springframework.stereotype.Component;

/** Niveau 2 (tenté, non garanti) : Campay (MTN MoMo, Orange Money). */
@Component("CAMPAY")
public class CampayPaiementProvider implements PaiementProvider {
    @Override
    public PaiementResultat payer(Reservation reservation, String telephonePaiement, String operateur) {
        // Campay abandonné pour cette version (pas de compte) : on garde SIMULE. Classe conservée pour une évolution.
        throw new UnsupportedOperationException("Campay pas encore branché");
    }
}
