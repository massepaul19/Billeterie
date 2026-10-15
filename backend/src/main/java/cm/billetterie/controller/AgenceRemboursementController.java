package cm.billetterie.controller;

import cm.billetterie.service.AgenceRemboursementService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur AgenceRemboursement - propriétaire : Back A (Balla) - Sprint 2 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/agence/remboursements
 * - PATCH /api/agence/remboursements/{id}
 */
@RestController
@RequestMapping("/api/agence/remboursements")
public class AgenceRemboursementController {

    private final AgenceRemboursementService service;

    public AgenceRemboursementController(AgenceRemboursementService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}