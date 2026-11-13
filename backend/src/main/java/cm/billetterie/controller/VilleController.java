package cm.billetterie.controller;

import cm.billetterie.dto.VilleDto;
import cm.billetterie.service.VilleService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/villes")
public class VilleController {

    private final VilleService service;

    public VilleController(VilleService service) {
        this.service = service;
    }

    @GetMapping
    public List<VilleDto> rechercher(@RequestParam(required = false) String q) {
        return service.rechercher(q);
    }
}
