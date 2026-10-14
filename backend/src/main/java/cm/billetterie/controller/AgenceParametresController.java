package cm.billetterie.controller;

import cm.billetterie.service.AgenceParametresService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceParametres - contrat : docs/API.md
 * Propriétaire : Back A - Sprint 2
 * Routes à coder :
 * - GET, PUT /rappels
 */
@RestController
@RequestMapping("/api/agence/parametres")
public class AgenceParametresController {

    private final AgenceParametresService service;

    public AgenceParametresController(AgenceParametresService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
