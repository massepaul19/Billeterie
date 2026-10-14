package cm.billetterie.controller;

import cm.billetterie.service.ReservationService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur Reservation - contrat : docs/API.md
 * Propriétaire : Back B - Sprint 2
 * Routes à coder :
 * - POST (bloque 5 min)
 * - GET /{id}
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
