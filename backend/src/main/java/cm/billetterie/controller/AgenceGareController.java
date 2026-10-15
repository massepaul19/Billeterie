package cm.billetterie.controller;

import cm.billetterie.service.AgenceGareService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceGare - propriétaire : Back A (Balla) - Sprint 1 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/agence/gares
 * - POST /api/agence/gares
 * - PUT /api/agence/gares/{id}
 * - DELETE /api/agence/gares/{id}
 */
@RestController
@RequestMapping("/api/agence/gares")
public class AgenceGareController {

    private final AgenceGareService service;

    public AgenceGareController(AgenceGareService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}