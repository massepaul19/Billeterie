package cm.billetterie.controller;

import cm.billetterie.service.AgenceVoyageService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceVoyage - contrat : docs/API.md
 * Propriétaire : Back A - Sprint 1
 * Routes à coder :
 * - GET, POST, PUT /{id}
 * - PATCH /{id}/prix
 * - POST /{id}/annulation (rembourse à 100 %)
 */
@RestController
@RequestMapping("/api/agence/voyages")
public class AgenceVoyageController {

    private final AgenceVoyageService service;

    public AgenceVoyageController(AgenceVoyageService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
