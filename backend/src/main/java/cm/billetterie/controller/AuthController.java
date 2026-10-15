package cm.billetterie.controller;

import cm.billetterie.service.AuthService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur Auth - propriétaire : Back A (Balla) - Sprint 1 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - POST /api/auth/connexion
 * - POST /api/auth/mot-de-passe
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}