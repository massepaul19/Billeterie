package cm.billetterie.controller;

import cm.billetterie.service.SuperAdminAgenceService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur SuperAdminAgence - propriétaire : Back A (Balla) - Sprint 1 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/admin/agences
 * - POST /api/admin/agences
 * - PUT /api/admin/agences/{id}
 * - DELETE /api/admin/agences/{id}
 * - PATCH /api/admin/agences/{id}/activation
 * - POST /api/admin/agences/{id}/compte
 * - POST /api/admin/agences/{id}/logo
 * - POST /api/admin/comptes/{id}/reinitialisation-mot-de-passe
 */
@RestController
@RequestMapping("/api/admin/agences")
public class SuperAdminAgenceController {

    private final SuperAdminAgenceService service;

    public SuperAdminAgenceController(SuperAdminAgenceService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}