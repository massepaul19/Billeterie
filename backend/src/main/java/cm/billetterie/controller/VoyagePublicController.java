package cm.billetterie.controller;

import cm.billetterie.service.VoyagePublicService;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur VoyagePublic - propriétaire : Back B (Patricia) - Sprint 1 - contrat : docs/API.md
 *
 * Routes à implémenter :
 * - GET /api/voyages?depart=&destination=&dateDebut=&dateFin=&heure=&type=&clim=&wifi=&q=&page=&taille=
 * - GET /api/voyages/{id}
 */
@RestController
@RequestMapping("/api/voyages")
public class VoyagePublicController {

    private final VoyagePublicService service;

    public VoyagePublicController(VoyagePublicService service) {
        this.service = service;
    }

    // TODO : coder les routes ci-dessus (DTO dans cm.billetterie.dto) et leurs tests
}