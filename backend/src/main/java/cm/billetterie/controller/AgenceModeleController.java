package cm.billetterie.controller;

import cm.billetterie.service.AgenceModeleService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceModele - contrat : docs/API.md
 * Propriétaire : Back A - Sprint 1
 * Routes à coder :
 * - CRUD + génération des voyages récurrents (OPTION)
 */
@RestController
@RequestMapping("/api/agence/modeles")
public class AgenceModeleController {

    private final AgenceModeleService service;

    public AgenceModeleController(AgenceModeleService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
