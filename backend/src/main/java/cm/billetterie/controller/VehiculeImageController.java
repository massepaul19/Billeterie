package cm.billetterie.controller;

import cm.billetterie.dto.ImageReponse;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.VehiculeImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/agence/vehicules")
public class VehiculeImageController {

    private final VehiculeImageService service;

    public VehiculeImageController(VehiculeImageService service) {
        this.service = service;
    }

    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public ImageReponse envoyer(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                @PathVariable Long id, @RequestParam("fichier") MultipartFile fichier) {
        return service.definir(auth.agenceId(), id, fichier);
    }
}
