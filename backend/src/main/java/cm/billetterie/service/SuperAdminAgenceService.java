package cm.billetterie.service;

import cm.billetterie.dto.*;
import cm.billetterie.entity.*;
import cm.billetterie.entity.enums.RoleCompte;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.*;
import cm.billetterie.util.Texte;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuperAdminAgenceService {

    private final AgenceRepository agences;
    private final VilleRepository villes;
    private final CompteAdminRepository comptes;
    private final GareRepository gares;
    private final VehiculeRepository vehicules;
    private final TrajetRepository trajets;
    private final ModeleAgenceRepository modeles;
    private final VoyageAgenceRepository voyages;
    private final PasswordEncoder encodeur;
    private final FichierStorageService fichiers;

    public SuperAdminAgenceService(AgenceRepository agences, VilleRepository villes, CompteAdminRepository comptes,
                                   GareRepository gares, VehiculeRepository vehicules, TrajetRepository trajets,
                                   ModeleAgenceRepository modeles, VoyageAgenceRepository voyages,
                                   PasswordEncoder encodeur, FichierStorageService fichiers) {
        this.agences = agences;
        this.villes = villes;
        this.comptes = comptes;
        this.gares = gares;
        this.vehicules = vehicules;
        this.trajets = trajets;
        this.modeles = modeles;
        this.voyages = voyages;
        this.encodeur = encodeur;
        this.fichiers = fichiers;
    }

    public List<AgenceAdminReponse> lister() {
        return agences.findAll().stream()
                .sorted(Comparator.comparing(Agence::getNom, String.CASE_INSENSITIVE_ORDER))
                .map(this::vers).toList();
    }

    @Transactional
    public AgenceAdminReponse creer(AgenceAdminRequete r) {
        Agence a = new Agence();
        a.setActif(true);
        a.setDevise("FCFA");
        a.setDelaisRappelHeures("24,2");
        return vers(agences.save(remplir(a, r)));
    }

    @Transactional
    public AgenceAdminReponse modifier(Long id, AgenceAdminRequete r) {
        return vers(agences.save(remplir(trouver(id), r)));
    }

    @Transactional
    public AgenceAdminReponse activer(Long id, boolean actif) {
        Agence a = trouver(id);
        a.setActif(actif);
        return vers(agences.save(a));
    }

    @Transactional
    public void supprimer(Long id) {
        Agence a = trouver(id);
        if (voyages.existsByAgenceId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "CONFLIT",
                    "Cette agence a des voyages : désactivez-la plutôt que de la supprimer.");
        }
        modeles.deleteAll(modeles.findByAgenceIdOrderById(id));
        modeles.flush();
        trajets.deleteAll(trajets.findByAgenceIdOrderById(id));
        trajets.flush();
        List<Vehicule> vs = vehicules.findByAgenceIdOrderByImmatriculation(id);
        vs.forEach(v -> fichiers.supprimer(v.getImage()));
        vehicules.deleteAll(vs);
        gares.deleteAll(gares.findByAgenceIdOrderByNom(id));
        comptes.deleteAll(comptes.findByAgenceId(id));
        comptes.flush();
        agences.delete(a);
        fichiers.supprimer(a.getLogo());
    }

    @Transactional
    public CompteReponse creerCompte(Long agenceId, CompteAgenceRequete r) {
        trouver(agenceId);
        String tel = Texte.telephone(r.telephone());
        if (tel == null || tel.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", "Téléphone invalide.");
        }
        if (comptes.findByTelephone(tel).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "CONFLIT", "Ce téléphone est déjà utilisé par un compte.");
        }
        CompteAdmin c = new CompteAdmin();
        c.setRole(RoleCompte.ADMIN_AGENCE);
        c.setAgenceId(agenceId);
        c.setTelephone(tel);
        c.setEmail(r.email());
        c.setMotDePasseHash(encodeur.encode(r.motDePasseInitial()));
        c.setActif(true);
        c = comptes.save(c);
        return new CompteReponse(c.getId(), c.getTelephone(), c.getEmail(), c.getRole(), c.getAgenceId(), true);
    }

    @Transactional
    public void reinitialiserMotDePasse(Long compteId, String nouveau) {
        CompteAdmin c = comptes.findById(compteId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Compte introuvable."));
        c.setMotDePasseHash(encodeur.encode(nouveau));
        comptes.save(c);
    }

    private Agence remplir(Agence a, AgenceAdminRequete r) {
        if (!villes.existsById(r.villeId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", "Ville inconnue.");
        }
        a.setNom(r.nom().trim());
        a.setSlogan(r.slogan());
        a.setVilleId(r.villeId());
        a.setLatitude(r.latitude());
        a.setLongitude(r.longitude());
        a.setNumeroMobileMoney(r.numeroMobileMoney());
        return a;
    }

    private Agence trouver(Long id) {
        return agences.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Agence introuvable."));
    }

    private AgenceAdminReponse vers(Agence a) {
        String ville = villes.findById(a.getVilleId()).map(Ville::getNom).orElse(null);
        String tel = comptes.findByAgenceId(a.getId()).stream().findFirst().map(CompteAdmin::getTelephone).orElse(null);
        return new AgenceAdminReponse(a.getId(), a.getNom(), fichiers.urlComplete(a.getLogo()), a.getSlogan(),
                a.getVilleId(), ville, a.getLatitude(), a.getLongitude(), a.getNumeroMobileMoney(),
                Boolean.TRUE.equals(a.getActif()), tel);
    }
}
