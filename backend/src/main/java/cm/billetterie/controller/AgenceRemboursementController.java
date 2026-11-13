package cm.billetterie.controller;

import cm.billetterie.dto.RemboursementAgenceReponse;
import cm.billetterie.dto.RemboursementStatutRequete;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.AgenceRemboursementService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agence/remboursements")
public class AgenceRemboursementController {

    private final AgenceRemboursementService service;

    public AgenceRemboursementController(AgenceRemboursementService service) {
        this.service = service;
    }

    @GetMapping
    public List<RemboursementAgenceReponse> lister(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth) {
        return service.lister(auth.agenceId());
    }

    @PatchMapping("/{id}")
    public RemboursementAgenceReponse changerStatut(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody RemboursementStatutRequete r) {
        return service.changerStatut(auth.agenceId(), id, r.statut());
    }
}
