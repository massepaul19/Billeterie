package cm.billetterie.controller;

import cm.billetterie.service.AgenceVehiculeService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceVehicule - contrat : docs/API.md
 * Propriétaire : Back A - Sprint 1
 * Routes à coder :
 * - CRUD complet
 */
@RestController
@RequestMapping("/api/agence/vehicules")
public class AgenceVehiculeController {

    private final AgenceVehiculeService service;

    public AgenceVehiculeController(AgenceVehiculeService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
