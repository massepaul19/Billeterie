package cm.billetterie.payment;

import cm.billetterie.entity.Reservation;
import cm.billetterie.entity.enums.StatutPaiement;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Niveau 1 (obligatoire) : paiement simulé, toujours disponible. */
@Component("SIMULE")
public class SimulePaiementProvider implements PaiementProvider {
    @Override
    public PaiementResultat payer(Reservation reservation, String telephonePaiement, String operateur) {
        // TODO (Back B) : option pour simuler aussi un ECHEC (ex. numéro finissant par 0)
        return new PaiementResultat("SIM-" + UUID.randomUUID(), StatutPaiement.REUSSI);
    }
}
