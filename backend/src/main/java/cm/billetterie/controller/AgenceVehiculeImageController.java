package cm.billetterie.controller;

import cm.billetterie.dto.ImageReponse;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.VehiculeImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/agence/vehicules/{id}/image")
public class AgenceVehiculeImageController {

    private final VehiculeImageService service;

    public AgenceVehiculeImageController(VehiculeImageService service) {
        this.service = service;
    }

    @PostMapping
    public ImageReponse envoyer(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                @PathVariable Long id, @RequestParam("fichier") MultipartFile fichier) {
        return service.televerser(auth.agenceId(), id, fichier);
    }
}
