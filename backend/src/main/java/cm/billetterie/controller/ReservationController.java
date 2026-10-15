package cm.billetterie.controller;

import cm.billetterie.service.ReservationService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur Reservation - propriétaire : Back B (Patricia) - Sprint 2 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - POST /api/reservations
 * - GET /api/reservations/{id}
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}