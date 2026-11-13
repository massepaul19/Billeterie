package cm.billetterie.controller;

import cm.billetterie.dto.ActivationRequete;
import cm.billetterie.dto.AgenceAdminReponse;
import cm.billetterie.dto.AgenceAdminRequete;
import cm.billetterie.service.SuperAdminAgenceService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/agences")
public class SuperAdminAgenceController {

    private final SuperAdminAgenceService service;

    public SuperAdminAgenceController(SuperAdminAgenceService service) {
        this.service = service;
    }

    @GetMapping
    public List<AgenceAdminReponse> lister() {
        return service.lister();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgenceAdminReponse creer(@Valid @RequestBody AgenceAdminRequete r) {
        return service.creer(r);
    }

    @PutMapping("/{id}")
    public AgenceAdminReponse modifier(@PathVariable Long id, @Valid @RequestBody AgenceAdminRequete r) {
        return service.modifier(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }

    @PatchMapping("/{id}/activation")
    public AgenceAdminReponse activation(@PathVariable Long id, @Valid @RequestBody ActivationRequete r) {
        return service.activer(id, r.actif());
    }
}
