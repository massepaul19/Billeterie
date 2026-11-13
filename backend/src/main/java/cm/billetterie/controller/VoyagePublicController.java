package cm.billetterie.controller;

import cm.billetterie.dto.PageReponse;
import cm.billetterie.dto.VoyageResume;
import cm.billetterie.entity.enums.TypeVehicule;
import cm.billetterie.service.VoyagePublicService;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voyages")
public class VoyagePublicController {

    private final VoyagePublicService service;

    public VoyagePublicController(VoyagePublicService service) {
        this.service = service;
    }

    @GetMapping
    public PageReponse<VoyageResume> rechercher(
            @RequestParam(required = false) String depart,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime heure,
            @RequestParam(required = false) TypeVehicule type,
            @RequestParam(required = false) Boolean clim,
            @RequestParam(required = false) Boolean wifi,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int taille) {
        return service.rechercher(depart, destination, dateDebut, dateFin, heure, type, clim, wifi, q,
                latitude, longitude, page, taille);
    }

    @GetMapping("/{id}")
    public VoyageResume detail(@PathVariable Long id) {
        return service.detail(id);
    }
}
