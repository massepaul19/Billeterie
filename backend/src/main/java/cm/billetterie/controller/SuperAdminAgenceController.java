package cm.billetterie.controller;

import cm.billetterie.service.SuperAdminAgenceService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur SuperAdminAgence - contrat : docs/API.md
 * Propriétaire : Back A - Sprint 1
 * Routes à coder :
 * - GET, POST, PUT /{id}, DELETE /{id}
 * - PATCH /{id}/activation
 * - POST /{id}/compte (créer le compte admin)
 * - POST /comptes/{id}/reinitialisation-mot-de-passe
 */
@RestController
@RequestMapping("/api/admin/agences")
public class SuperAdminAgenceController {

    private final SuperAdminAgenceService service;

    public SuperAdminAgenceController(SuperAdminAgenceService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
