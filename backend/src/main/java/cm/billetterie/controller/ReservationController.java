package cm.billetterie.controller;

import cm.billetterie.dto.ReservationReponse;
import cm.billetterie.dto.ReservationRequete;
import cm.billetterie.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationReponse creer(@Valid @RequestBody ReservationRequete requete, HttpServletRequest http) {
        return service.creer(requete, ipClient(http));
    }

    @GetMapping("/{id}")
    public ReservationReponse obtenir(@PathVariable Long id) {
        return service.obtenir(id);
    }

    /** IP du client, en tenant compte d'un proxy (nginx) devant l'application. */
    static String ipClient(HttpServletRequest http) {
        String forwarded = http.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        return http.getRemoteAddr();
    }
}
