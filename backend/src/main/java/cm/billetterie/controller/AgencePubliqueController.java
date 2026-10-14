package cm.billetterie.controller;

import cm.billetterie.service.AgencePubliqueService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgencePublique - contrat : docs/API.md
 * Propriétaire : Back B - Sprint 1
 * Routes à coder :
 * - GET (liste pour la carte)
 * - GET /{id}
 */
@RestController
@RequestMapping("/api/agences")
public class AgencePubliqueController {

    private final AgencePubliqueService service;

    public AgencePubliqueController(AgencePubliqueService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
