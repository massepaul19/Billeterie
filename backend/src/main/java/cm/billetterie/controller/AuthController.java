package cm.billetterie.controller;

import cm.billetterie.dto.ConnexionReponse;
import cm.billetterie.dto.ConnexionRequete;
import cm.billetterie.dto.MotDePasseRequete;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/connexion")
    public ConnexionReponse connexion(@Valid @RequestBody ConnexionRequete requete) {
        return service.connexion(requete);
    }

    @PostMapping("/mot-de-passe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changerMotDePasse(@RequestAttribute(AuthPrincipal.ATTRIBUT) AuthPrincipal auth,
                                  @Valid @RequestBody MotDePasseRequete requete) {
        service.changerMotDePasse(auth, requete);
    }
}
