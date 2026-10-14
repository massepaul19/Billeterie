package cm.billetterie.controller;

import cm.billetterie.service.AgenceTrajetService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceTrajet - contrat : docs/API.md
 * Propriétaire : Back A - Sprint 1
 * Routes à coder :
 * - CRUD complet
 */
@RestController
@RequestMapping("/api/agence/trajets")
public class AgenceTrajetController {

    private final AgenceTrajetService service;

    public AgenceTrajetController(AgenceTrajetService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
