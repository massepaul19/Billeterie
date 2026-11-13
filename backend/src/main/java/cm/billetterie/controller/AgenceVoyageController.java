package cm.billetterie.controller;

import cm.billetterie.dto.*;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.AgenceVoyageService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agence/voyages")
public class AgenceVoyageController {

    private final AgenceVoyageService service;

    public AgenceVoyageController(AgenceVoyageService service) {
        this.service = service;
    }

    @GetMapping
    public List<VoyageAgenceReponse> lister(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth) {
        return service.lister(auth.agenceId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VoyageAgenceReponse creer(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                     @Valid @RequestBody VoyageAgenceRequete r) {
        return service.creer(auth.agenceId(), r);
    }

    @PutMapping("/{id}")
    public VoyageAgenceReponse modifier(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                        @PathVariable Long id, @Valid @RequestBody VoyageAgenceRequete r) {
        return service.modifier(auth.agenceId(), id, r);
    }

    @PatchMapping("/{id}/prix")
    public VoyageAgenceReponse prix(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                    @PathVariable Long id, @Valid @RequestBody PrixRequete r) {
        return service.changerPrix(auth.agenceId(), id, r.prix());
    }

    @PostMapping("/{id}/annulation")
    public AnnulationVoyageReponse annuler(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                           @PathVariable Long id) {
        return service.annuler(auth.agenceId(), id);
    }
}
