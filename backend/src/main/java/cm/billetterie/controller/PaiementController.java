package cm.billetterie.controller;

import cm.billetterie.service.PaiementService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur Paiement - contrat : docs/API.md
 * Propriétaire : Back B - Sprint 2
 * Routes à coder :
 * - POST
 * - GET /{id}
 */
@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService service;

    public PaiementController(PaiementService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
