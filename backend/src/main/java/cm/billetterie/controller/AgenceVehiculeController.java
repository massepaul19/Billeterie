package cm.billetterie.controller;

import cm.billetterie.dto.VehiculeReponse;
import cm.billetterie.dto.VehiculeRequete;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.AgenceVehiculeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agence/vehicules")
public class AgenceVehiculeController {

    private final AgenceVehiculeService service;

    public AgenceVehiculeController(AgenceVehiculeService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehiculeReponse> lister(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth) {
        return service.lister(auth.agenceId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeReponse creer(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                 @Valid @RequestBody VehiculeRequete r) {
        return service.creer(auth.agenceId(), r);
    }

    @PutMapping("/{id}")
    public VehiculeReponse modifier(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                    @PathVariable Long id, @Valid @RequestBody VehiculeRequete r) {
        return service.modifier(auth.agenceId(), id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth, @PathVariable Long id) {
        service.supprimer(auth.agenceId(), id);
    }
}
