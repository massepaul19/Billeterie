package cm.billetterie.controller;

import cm.billetterie.dto.AgenceCarteDto;
import cm.billetterie.service.AgencePubliqueService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agences")
public class AgencePubliqueController {

    private final AgencePubliqueService service;

    public AgencePubliqueController(AgencePubliqueService service) {
        this.service = service;
    }

    @GetMapping
    public List<AgenceCarteDto> lister() {
        return service.lister();
    }
}
