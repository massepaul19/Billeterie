package cm.billetterie.controller;

import cm.billetterie.dto.GareReponse;
import cm.billetterie.dto.GareRequete;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.AgenceGareService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agence/gares")
public class AgenceGareController {

    private final AgenceGareService service;

    public AgenceGareController(AgenceGareService service) {
        this.service = service;
    }

    @GetMapping
    public List<GareReponse> lister(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth) {
        return service.lister(auth.agenceId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GareReponse creer(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                             @Valid @RequestBody GareRequete r) {
        return service.creer(auth.agenceId(), r);
    }

    @PutMapping("/{id}")
    public GareReponse modifier(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                @PathVariable Long id, @Valid @RequestBody GareRequete r) {
        return service.modifier(auth.agenceId(), id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth, @PathVariable Long id) {
        service.supprimer(auth.agenceId(), id);
    }
}
