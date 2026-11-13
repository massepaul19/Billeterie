package cm.billetterie.service;

import cm.billetterie.dto.GareReponse;
import cm.billetterie.dto.GareRequete;
import cm.billetterie.entity.Gare;
import cm.billetterie.entity.Ville;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.GareRepository;
import cm.billetterie.repository.TrajetRepository;
import cm.billetterie.repository.VilleRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgenceGareService {

    private final GareRepository gares;
    private final VilleRepository villes;
    private final TrajetRepository trajets;

    public AgenceGareService(GareRepository gares, VilleRepository villes, TrajetRepository trajets) {
        this.gares = gares;
        this.villes = villes;
        this.trajets = trajets;
    }

    public List<GareReponse> lister(Long agenceId) {
        return gares.findByAgenceIdOrderByNom(agenceId).stream().map(this::vers).toList();
    }

    @Transactional
    public GareReponse creer(Long agenceId, GareRequete r) {
        Gare g = new Gare();
        g.setAgenceId(agenceId);
        return vers(gares.save(remplir(g, r)));
    }

    @Transactional
    public GareReponse modifier(Long agenceId, Long id, GareRequete r) {
        Gare g = gares.findByIdAndAgenceId(id, agenceId).orElseThrow(this::introuvable);
        return vers(gares.save(remplir(g, r)));
    }

    @Transactional
    public void supprimer(Long agenceId, Long id) {
        Gare g = gares.findByIdAndAgenceId(id, agenceId).orElseThrow(this::introuvable);
        if (trajets.existsByGareDepartIdOrGareArriveeId(id, id)) {
            throw new ApiException(HttpStatus.CONFLICT, "CONFLIT",
                    "Cette gare est utilisée par un trajet : supprimez d'abord le trajet.");
        }
        gares.delete(g);
    }

    private Gare remplir(Gare g, GareRequete r) {
        if (!villes.existsById(r.villeId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", "Ville inconnue.");
        }
        g.setVilleId(r.villeId());
        g.setNom(r.nom().trim());
        g.setQuartier(r.quartier());
        g.setAdresse(r.adresse());
        g.setLatitude(r.latitude());
        g.setLongitude(r.longitude());
        return g;
    }

    private GareReponse vers(Gare g) {
        String ville = villes.findById(g.getVilleId()).map(Ville::getNom).orElse(null);
        return new GareReponse(g.getId(), g.getVilleId(), ville, g.getNom(), g.getQuartier(), g.getAdresse(),
                g.getLatitude(), g.getLongitude());
    }

    private ApiException introuvable() {
        return new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Gare introuvable.");
    }
}
