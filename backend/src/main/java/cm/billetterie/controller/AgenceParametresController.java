package cm.billetterie.controller;

import cm.billetterie.dto.RappelsDto;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.AgenceParametresService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agence/parametres")
public class AgenceParametresController {

    private final AgenceParametresService service;

    public AgenceParametresController(AgenceParametresService service) {
        this.service = service;
    }

    @GetMapping("/rappels")
    public RappelsDto lire(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth) {
        return service.lireRappels(auth.agenceId());
    }

    @PutMapping("/rappels")
    public RappelsDto modifier(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                               @Valid @RequestBody RappelsDto r) {
        return service.modifierRappels(auth.agenceId(), r);
    }
}
