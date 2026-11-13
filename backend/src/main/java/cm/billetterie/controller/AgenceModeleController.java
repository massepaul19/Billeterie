package cm.billetterie.controller;

import cm.billetterie.dto.ModeleReponse;
import cm.billetterie.dto.ModeleRequete;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.AgenceModeleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agence/modeles")
public class AgenceModeleController {

    private final AgenceModeleService service;

    public AgenceModeleController(AgenceModeleService service) {
        this.service = service;
    }

    @GetMapping
    public List<ModeleReponse> lister(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth) {
        return service.lister(auth.agenceId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ModeleReponse creer(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                               @Valid @RequestBody ModeleRequete r) {
        return service.creer(auth.agenceId(), r);
    }

    @PutMapping("/{id}")
    public ModeleReponse modifier(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                  @PathVariable Long id, @Valid @RequestBody ModeleRequete r) {
        return service.modifier(auth.agenceId(), id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth, @PathVariable Long id) {
        service.supprimer(auth.agenceId(), id);
    }
}
