package cm.billetterie.controller;

import cm.billetterie.service.SuperAdminParametreService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur SuperAdminParametre - propriétaire : Back A (Balla) - Sprint 2 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/admin/parametres/commission
 * - PUT /api/admin/parametres/commission
 */
@RestController
@RequestMapping("/api/admin/parametres")
public class SuperAdminParametreController {

    private final SuperAdminParametreService service;

    public SuperAdminParametreController(SuperAdminParametreService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}