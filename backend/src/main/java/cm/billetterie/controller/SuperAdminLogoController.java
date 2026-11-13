package cm.billetterie.controller;

import cm.billetterie.dto.LogoReponse;
import cm.billetterie.service.AgenceLogoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** Logo d'une agence, côté super administrateur. */
@RestController
@RequestMapping("/api/admin/agences/{id}/logo")
public class SuperAdminLogoController {

    private final AgenceLogoService service;

    public SuperAdminLogoController(AgenceLogoService service) {
        this.service = service;
    }

    @PostMapping
    public LogoReponse envoyer(@PathVariable Long id, @RequestParam("fichier") MultipartFile fichier) {
        return service.televerser(id, fichier);
    }
}
