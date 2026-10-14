package cm.billetterie.controller;

import cm.billetterie.service.AgenceGareService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceGare - contrat : docs/API.md
 * Propriétaire : Back A - Sprint 1
 * Routes à coder :
 * - CRUD complet (filtré par agenceId du jeton)
 */
@RestController
@RequestMapping("/api/agence/gares")
public class AgenceGareController {

    private final AgenceGareService service;

    public AgenceGareController(AgenceGareService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
