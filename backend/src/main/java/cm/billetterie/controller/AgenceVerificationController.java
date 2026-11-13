package cm.billetterie.controller;

import cm.billetterie.dto.BilletAgenceReponse;
import cm.billetterie.dto.VerificationRequete;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.AgenceVerificationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agence/billets")
public class AgenceVerificationController {

    private final AgenceVerificationService service;

    public AgenceVerificationController(AgenceVerificationService service) {
        this.service = service;
    }

    @PostMapping("/verification")
    public BilletAgenceReponse verifier(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                        @Valid @RequestBody VerificationRequete r) {
        return service.verifier(auth.agenceId(), r.code());
    }

    @GetMapping("/recherche")
    public List<BilletAgenceReponse> rechercher(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                                @RequestParam String q) {
        return service.rechercher(auth.agenceId(), q);
    }
}
