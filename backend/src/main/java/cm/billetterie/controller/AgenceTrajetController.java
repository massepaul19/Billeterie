package cm.billetterie.controller;

import cm.billetterie.service.AgenceTrajetService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceTrajet - propriétaire : Back B (Patricia) - Sprint 1 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/agence/trajets
 * - POST /api/agence/trajets
 * - PUT /api/agence/trajets/{id}
 * - DELETE /api/agence/trajets/{id}
 */
@RestController
@RequestMapping("/api/agence/trajets")
public class AgenceTrajetController {

    private final AgenceTrajetService service;

    public AgenceTrajetController(AgenceTrajetService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}