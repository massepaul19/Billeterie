package cm.billetterie.service;

import cm.billetterie.dto.ConnexionReponse;
import cm.billetterie.dto.ConnexionRequete;
import cm.billetterie.dto.MotDePasseRequete;
import cm.billetterie.entity.CompteAdmin;
import cm.billetterie.entity.enums.RoleCompte;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.AgenceRepository;
import cm.billetterie.repository.CompteAdminRepository;
import cm.billetterie.security.AuthPrincipal;
import cm.billetterie.security.JwtService;
import cm.billetterie.util.Texte;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final CompteAdminRepository comptes;
    private final AgenceRepository agences;
    private final PasswordEncoder encodeur;
    private final JwtService jwt;

    public AuthService(CompteAdminRepository comptes, AgenceRepository agences,
                       PasswordEncoder encodeur, JwtService jwt) {
        this.comptes = comptes;
        this.agences = agences;
        this.encodeur = encodeur;
        this.jwt = jwt;
    }

    public ConnexionReponse connexion(ConnexionRequete r) {
        String brut = r.telephone().trim();
        CompteAdmin c = comptes.findByTelephone(Texte.telephone(brut))
                .or(() -> comptes.findByTelephone(brut)).orElse(null);
        if (c == null || c.getMotDePasseHash() == null
                || !encodeur.matches(r.motDePasse(), c.getMotDePasseHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "NON_AUTHENTIFIE",
                    "Téléphone ou mot de passe incorrect.");
        }
        if (!Boolean.TRUE.equals(c.getActif())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "INTERDIT", "Ce compte est désactivé.");
        }
        // RG16 : agence désactivée => connexion refusée
        if (c.getRole() == RoleCompte.ADMIN_AGENCE) {
            boolean active = c.getAgenceId() != null && agences.findById(c.getAgenceId())
                    .map(a -> Boolean.TRUE.equals(a.getActif())).orElse(false);
            if (!active) {
                throw new ApiException(HttpStatus.FORBIDDEN, "INTERDIT", "Votre agence est désactivée.");
            }
        }
        return new ConnexionReponse(jwt.generer(c), c.getRole(), c.getAgenceId());
    }

    @Transactional
    public void changerMotDePasse(AuthPrincipal auth, MotDePasseRequete r) {
        CompteAdmin c = comptes.findById(auth.compteId())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "NON_AUTHENTIFIE", "Compte introuvable."));
        if (!encodeur.matches(r.ancien(), c.getMotDePasseHash())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", "L'ancien mot de passe est incorrect.");
        }
        c.setMotDePasseHash(encodeur.encode(r.nouveau()));
        comptes.save(c);
    }
}
