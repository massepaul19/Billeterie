package cm.billetterie.controller;

import cm.billetterie.service.SuperAdminCommissionService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur SuperAdminCommission - contrat : docs/API.md
 * Propriétaire : Back A - Sprint 2
 * Routes à coder :
 * - GET ?mois=YYYY-MM
 */
@RestController
@RequestMapping("/api/admin/commissions")
public class SuperAdminCommissionController {

    private final SuperAdminCommissionService service;

    public SuperAdminCommissionController(SuperAdminCommissionService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
