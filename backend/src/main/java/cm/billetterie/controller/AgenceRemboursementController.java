package cm.billetterie.controller;

import cm.billetterie.service.AgenceRemboursementService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceRemboursement - contrat : docs/API.md
 * Propriétaire : Back A - Sprint 2
 * Routes à coder :
 * - GET
 * - PATCH /{id} (statut)
 */
@RestController
@RequestMapping("/api/agence/remboursements")
public class AgenceRemboursementController {

    private final AgenceRemboursementService service;

    public AgenceRemboursementController(AgenceRemboursementService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
