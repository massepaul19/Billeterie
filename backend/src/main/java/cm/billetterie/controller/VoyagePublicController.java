package cm.billetterie.controller;

import cm.billetterie.service.VoyagePublicService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur VoyagePublic - contrat : docs/API.md
 * Propriétaire : Back B - Sprint 1
 * Routes à coder :
 * - GET (recherche dynamique, paginée)
 * - GET /{id}
 */
@RestController
@RequestMapping("/api/voyages")
public class VoyagePublicController {

    private final VoyagePublicService service;

    public VoyagePublicController(VoyagePublicService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto), tests dans src/test
}
