package cm.billetterie.controller;

import cm.billetterie.service.PaiementService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur Paiement - propriétaire : Back B (Patricia) - Sprint 2 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - POST /api/paiements
 */
@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService service;

    public PaiementController(PaiementService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}