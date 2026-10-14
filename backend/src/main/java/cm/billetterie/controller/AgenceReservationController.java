package cm.billetterie.controller;

import cm.billetterie.service.AgenceReservationService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceReservation - contrat : docs/API.md
 * Propriétaire : Back B - Sprint 2
 * Routes à coder :
 * - GET (liste filtrée)
 */
@RestController
@RequestMapping("/api/agence/reservations")
public class AgenceReservationController {

    private final AgenceReservationService service;

    public AgenceReservationController(AgenceReservationService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
