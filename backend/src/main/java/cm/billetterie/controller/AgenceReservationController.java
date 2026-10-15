package cm.billetterie.controller;

import cm.billetterie.service.AgenceReservationService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceReservation - propriétaire : Back B (Patricia) - Sprint 2 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/agence/reservations?statut=&voyageId=
 */
@RestController
@RequestMapping("/api/agence/reservations")
public class AgenceReservationController {

    private final AgenceReservationService service;

    public AgenceReservationController(AgenceReservationService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}