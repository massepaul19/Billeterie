package cm.billetterie.controller;

import cm.billetterie.service.BilletService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur Billet - propriétaire : Back B (Patricia) - Sprint 2 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/billets/{code}
 * - GET /api/billets/{code}/pdf
 * - POST /api/billets/{code}/annulation
 * - POST /api/billets/{code}/reutilisation
 */
@RestController
@RequestMapping("/api/billets")
public class BilletController {

    private final BilletService service;

    public BilletController(BilletService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}