package cm.billetterie.controller;

import cm.billetterie.service.AgenceLogoService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceLogo - propriétaire : Back A (Balla) - Sprint 1 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - POST /api/agence/logo
 */
@RestController
@RequestMapping("/api/agence/logo")
public class AgenceLogoController {

    private final AgenceLogoService service;

    public AgenceLogoController(AgenceLogoService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}