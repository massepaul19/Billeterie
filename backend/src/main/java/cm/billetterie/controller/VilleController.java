package cm.billetterie.controller;

import cm.billetterie.service.VilleService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur Ville - contrat : docs/API.md
 * Propriétaire : Back B - Sprint 1
 * Routes à coder :
 * - GET ?q= (suggestions)
 */
@RestController
@RequestMapping("/api/villes")
public class VilleController {

    private final VilleService service;

    public VilleController(VilleService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
