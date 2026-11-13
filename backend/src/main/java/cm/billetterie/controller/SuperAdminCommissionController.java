package cm.billetterie.controller;

import cm.billetterie.dto.CommissionReponse;
import cm.billetterie.service.SuperAdminCommissionService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/commissions")
public class SuperAdminCommissionController {

    private final SuperAdminCommissionService service;

    public SuperAdminCommissionController(SuperAdminCommissionService service) {
        this.service = service;
    }

    @GetMapping
    public List<CommissionReponse> lister(@RequestParam(name = "mois", required = false) String mois) {
        String m = (mois == null || mois.isBlank())
                ? LocalDate.now(ZoneId.of("Africa/Douala")).toString().substring(0, 7)
                : mois;
        return service.lister(m);
    }
}
