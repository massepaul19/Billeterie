package cm.billetterie.controller;

import cm.billetterie.service.AgencePubliqueService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgencePublique - propriétaire : Back B (Patricia) - Sprint 1 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/agences
 */
@RestController
@RequestMapping("/api/agences")
public class AgencePubliqueController {

    private final AgencePubliqueService service;

    public AgencePubliqueController(AgencePubliqueService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}