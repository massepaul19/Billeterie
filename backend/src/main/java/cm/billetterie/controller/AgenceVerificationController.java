package cm.billetterie.controller;

import cm.billetterie.service.AgenceVerificationService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceVerification - propriétaire : Back B (Patricia) - Sprint 2 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - POST /api/agence/billets/verification
 * - GET /api/agence/billets/recherche?q=
 */
@RestController
@RequestMapping("/api/agence/billets")
public class AgenceVerificationController {

    private final AgenceVerificationService service;

    public AgenceVerificationController(AgenceVerificationService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}