package cm.billetterie.controller;

import cm.billetterie.service.AgenceVoyageService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceVoyage - propriétaire : Back B (Patricia) - Sprint 1 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/agence/voyages
 * - POST /api/agence/voyages
 * - PUT /api/agence/voyages/{id}
 * - PATCH /api/agence/voyages/{id}/prix
 * - POST /api/agence/voyages/{id}/annulation
 */
@RestController
@RequestMapping("/api/agence/voyages")
public class AgenceVoyageController {

    private final AgenceVoyageService service;

    public AgenceVoyageController(AgenceVoyageService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}