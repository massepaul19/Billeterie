package cm.billetterie.controller;

import cm.billetterie.dto.TrajetReponse;
import cm.billetterie.dto.TrajetRequete;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.AgenceTrajetService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agence/trajets")
public class AgenceTrajetController {

    private final AgenceTrajetService service;

    public AgenceTrajetController(AgenceTrajetService service) {
        this.service = service;
    }

    @GetMapping
    public List<TrajetReponse> lister(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth) {
        return service.lister(auth.agenceId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrajetReponse creer(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                               @Valid @RequestBody TrajetRequete r) {
        return service.creer(auth.agenceId(), r);
    }

    @PutMapping("/{id}")
    public TrajetReponse modifier(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                  @PathVariable Long id, @Valid @RequestBody TrajetRequete r) {
        return service.modifier(auth.agenceId(), id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth, @PathVariable Long id) {
        service.supprimer(auth.agenceId(), id);
    }
}
