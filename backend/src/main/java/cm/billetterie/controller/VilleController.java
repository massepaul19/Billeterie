package cm.billetterie.controller;

import cm.billetterie.service.VilleService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur Ville - propriétaire : Back B (Patricia) - Sprint 1 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/villes?q=
 */
@RestController
@RequestMapping("/api/villes")
public class VilleController {

    private final VilleService service;

    public VilleController(VilleService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}