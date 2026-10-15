package cm.billetterie.controller;

import cm.billetterie.service.AgenceVehiculeService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceVehicule - propriétaire : Back A (Balla) - Sprint 1 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/agence/vehicules
 * - POST /api/agence/vehicules
 * - PUT /api/agence/vehicules/{id}
 * - DELETE /api/agence/vehicules/{id}
 * - POST /api/agence/vehicules/{id}/image
 */
@RestController
@RequestMapping("/api/agence/vehicules")
public class AgenceVehiculeController {

    private final AgenceVehiculeService service;

    public AgenceVehiculeController(AgenceVehiculeService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}