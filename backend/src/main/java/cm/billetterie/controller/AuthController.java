package cm.billetterie.controller;

import cm.billetterie.service.AuthService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur Auth - contrat : docs/API.md
 * Propriétaire : Back A - Sprint 1
 * Routes à coder :
 * - POST /connexion
 * - POST /mot-de-passe (changer)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
