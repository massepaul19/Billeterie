package cm.billetterie.controller;

import cm.billetterie.dto.CommissionTauxDto;
import cm.billetterie.service.SuperAdminParametreService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/parametres")
public class SuperAdminParametreController {

    private final SuperAdminParametreService service;

    public SuperAdminParametreController(SuperAdminParametreService service) {
        this.service = service;
    }

    @GetMapping("/commission")
    public CommissionTauxDto lire() {
        return service.lireCommission();
    }

    @PutMapping("/commission")
    public CommissionTauxDto modifier(@Valid @RequestBody CommissionTauxDto r) {
        return service.modifierCommission(r);
    }
}
