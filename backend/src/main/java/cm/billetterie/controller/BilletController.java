package cm.billetterie.controller;

import cm.billetterie.service.BilletService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur Billet - contrat : docs/API.md
 * Propriétaire : Back B - Sprint 2
 * Routes à coder :
 * - GET /{code}
 * - GET /{code}/pdf
 * - POST /{code}/annulation
 * - POST /{code}/reutilisation (OPTION)
 */
@RestController
@RequestMapping("/api/billets")
public class BilletController {

    private final BilletService service;

    public BilletController(BilletService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
