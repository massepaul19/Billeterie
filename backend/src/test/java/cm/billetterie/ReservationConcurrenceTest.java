package cm.billetterie;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ReservationConcurrenceTest {

    @Disabled("TODO (Back B, Sprint 1) : tester VoyageRepository.reserverUnePlace (5 places, 50 threads) puis activer")
    @Test
    void aucunePlaceVendueDeuxFois() {
        // Voyage de 5 places, 50 threads qui réservent en même temps :
        // exactement 5 réservations réussies, places_restantes == 0, jamais négatif.
    }
}
