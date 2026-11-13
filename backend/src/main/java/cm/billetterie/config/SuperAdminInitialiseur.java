package cm.billetterie.config;

import cm.billetterie.entity.CompteAdmin;
import cm.billetterie.entity.enums.RoleCompte;
import cm.billetterie.repository.CompteAdminRepository;
import cm.billetterie.util.Texte;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Crée le premier super administrateur s'il n'en existe aucun. */
@Component
@Order(1000)
public class SuperAdminInitialiseur implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminInitialiseur.class);
    private static final String ALPHABET = "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final CompteAdminRepository comptes;
    private final PasswordEncoder encodeur;
    private final String telephone;
    private final String email;
    private final String motDePasse;

    public SuperAdminInitialiseur(CompteAdminRepository comptes, PasswordEncoder encodeur,
                                  @Value("${billetterie.admin.telephone:600000000}") String telephone,
                                  @Value("${billetterie.admin.email:admin@billetterie.local}") String email,
                                  @Value("${billetterie.admin.mot-de-passe:}") String motDePasse) {
        this.comptes = comptes;
        this.encodeur = encodeur;
        this.telephone = telephone;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (comptes.existsByRole(RoleCompte.SUPER_ADMIN)) return;
        String mdp = motDePasse == null || motDePasse.isBlank() ? aleatoire(14) : motDePasse;

        CompteAdmin c = new CompteAdmin();
        c.setRole(RoleCompte.SUPER_ADMIN);
        c.setTelephone(Texte.telephone(telephone));
        c.setEmail(email);
        c.setMotDePasseHash(encodeur.encode(mdp));
        c.setActif(true);
        comptes.save(c);

        if (motDePasse == null || motDePasse.isBlank()) {
            log.warn("Super administrateur créé. Téléphone : {} - mot de passe généré : {} (à changer)",
                    c.getTelephone(), mdp);
        } else {
            log.info("Super administrateur créé (téléphone : {}).", c.getTelephone());
        }
    }

    private static String aleatoire(int n) {
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(ALPHABET.charAt(r.nextInt(ALPHABET.length())));
        return sb.toString();
    }
}
