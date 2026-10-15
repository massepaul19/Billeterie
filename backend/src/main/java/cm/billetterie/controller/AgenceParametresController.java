package cm.billetterie.controller;

import cm.billetterie.service.AgenceParametresService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceParametres - propriétaire : Back A (Balla) - Sprint 2 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/agence/parametres/rappels
 * - PUT /api/agence/parametres/rappels
 */
@RestController
@RequestMapping("/api/agence/parametres")
public class AgenceParametresController {

    private final AgenceParametresService service;

    public AgenceParametresController(AgenceParametresService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}