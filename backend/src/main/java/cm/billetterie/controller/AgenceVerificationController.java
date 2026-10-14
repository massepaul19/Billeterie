package cm.billetterie.controller;

import cm.billetterie.service.AgenceVerificationService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceVerification - contrat : docs/API.md
 * Propriétaire : Back B - Sprint 2
 * Routes à coder :
 * - POST /verification {code}
 * - GET /recherche?q= (nom ou téléphone)
 */
@RestController
@RequestMapping("/api/agence/billets")
public class AgenceVerificationController {

    private final AgenceVerificationService service;

    public AgenceVerificationController(AgenceVerificationService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
