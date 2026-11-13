package cm.billetterie.controller;

import cm.billetterie.dto.PageReponse;
import cm.billetterie.dto.ReservationAgenceReponse;
import cm.billetterie.entity.enums.StatutReservation;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.AgenceReservationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agence/reservations")
public class AgenceReservationController {

    private final AgenceReservationService service;

    public AgenceReservationController(AgenceReservationService service) {
        this.service = service;
    }

    @GetMapping
    public PageReponse<ReservationAgenceReponse> lister(
            @RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
            @RequestParam(required = false) StatutReservation statut,
            @RequestParam(required = false) Long voyageId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int taille) {
        return service.lister(auth.agenceId(), statut, voyageId, page, taille);
    }
}
