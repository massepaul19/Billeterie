package cm.billetterie.controller;

import cm.billetterie.dto.LogoReponse;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.AgenceLogoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** Logo de sa propre agence (contrat : POST /api/agence/logo, champ « fichier »). */
@RestController
@RequestMapping("/api/agence/logo")
public class AgenceLogoController {

    private final AgenceLogoService service;

    public AgenceLogoController(AgenceLogoService service) {
        this.service = service;
    }

    @PostMapping
    public LogoReponse envoyer(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                               @RequestParam("fichier") MultipartFile fichier) {
        return service.televerser(auth.agenceId(), fichier);
    }
}
