package cm.billetterie.controller;

import cm.billetterie.service.AgenceModeleService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceModele - propriétaire : Back B (Patricia) - Sprint 1 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/agence/modeles
 * - POST /api/agence/modeles
 * - PUT /api/agence/modeles/{id}
 * - DELETE /api/agence/modeles/{id}
 */
@RestController
@RequestMapping("/api/agence/modeles")
public class AgenceModeleController {

    private final AgenceModeleService service;

    public AgenceModeleController(AgenceModeleService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}