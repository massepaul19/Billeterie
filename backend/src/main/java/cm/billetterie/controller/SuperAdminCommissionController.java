package cm.billetterie.controller;

import cm.billetterie.service.SuperAdminCommissionService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur SuperAdminCommission - propriétaire : Back A (Balla) - Sprint 2 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/admin/commissions?mois=YYYY-MM
 */
@RestController
@RequestMapping("/api/admin/commissions")
public class SuperAdminCommissionController {

    private final SuperAdminCommissionService service;

    public SuperAdminCommissionController(SuperAdminCommissionService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}