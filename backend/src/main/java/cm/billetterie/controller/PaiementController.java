package cm.billetterie.controller;

import cm.billetterie.dto.PaiementReponse;
import cm.billetterie.dto.PaiementRequete;
import cm.billetterie.service.PaiementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService service;

    public PaiementController(PaiementService service) {
        this.service = service;
    }

    @PostMapping
    public PaiementReponse payer(@Valid @RequestBody PaiementRequete requete) {
        return service.payer(requete);
    }
}
