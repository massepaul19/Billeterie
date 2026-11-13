package cm.billetterie.service;

import cm.billetterie.dto.TrajetReponse;
import cm.billetterie.dto.TrajetRequete;
import cm.billetterie.entity.Gare;
import cm.billetterie.entity.Trajet;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.GareRepository;
import cm.billetterie.repository.ModeleLiaisonRepository;
import cm.billetterie.repository.TrajetRepository;
import cm.billetterie.repository.VoyageLiaisonRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgenceTrajetService {

    private final TrajetRepository trajets;
    private final GareRepository gares;
    private final VoyageLiaisonRepository voyages;
    private final ModeleLiaisonRepository modeles;

    public AgenceTrajetService(TrajetRepository trajets, GareRepository gares,
                               VoyageLiaisonRepository voyages, ModeleLiaisonRepository modeles) {
        this.trajets = trajets;
        this.gares = gares;
        this.voyages = voyages;
        this.modeles = modeles;
    }

    public List<TrajetReponse> lister(Long agenceId) {
        return trajets.findByAgenceIdOrderById(agenceId).stream().map(this::vers).toList();
    }

    @Transactional
    public TrajetReponse creer(Long agenceId, TrajetRequete r) {
        Trajet t = new Trajet();
        t.setAgenceId(agenceId);
        return vers(trajets.save(remplir(t, r)));
    }

    @Transactional
    public TrajetReponse modifier(Long agenceId, Long id, TrajetRequete r) {
        Trajet t = trajets.findByIdAndAgenceId(id, agenceId).orElseThrow(this::introuvable);
        return vers(trajets.save(remplir(t, r)));
    }

    @Transactional
    public void supprimer(Long agenceId, Long id) {
        Trajet t = trajets.findByIdAndAgenceId(id, agenceId).orElseThrow(this::introuvable);
        if (voyages.existsByTrajetId(id) || modeles.existsByTrajetId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "CONFLIT",
                    "Ce trajet est utilisé par un voyage ou un modèle de voyage.");
        }
        trajets.delete(t);
    }

    private Trajet remplir(Trajet t, TrajetRequete r) {
        if (r.gareDepartId().equals(r.gareArriveeId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION",
                    "La gare de départ et la gare d'arrivée doivent être différentes.");
        }
        // les deux gares doivent appartenir à l'agence du jeton
        gares.findByIdAndAgenceId(r.gareDepartId(), t.getAgenceId()).orElseThrow(this::gareInconnue);
        gares.findByIdAndAgenceId(r.gareArriveeId(), t.getAgenceId()).orElseThrow(this::gareInconnue);
        t.setGareDepartId(r.gareDepartId());
        t.setGareArriveeId(r.gareArriveeId());
        t.setDureeEstimeeMin(r.dureeEstimeeMin());
        return t;
    }

    private TrajetReponse vers(Trajet t) {
        return new TrajetReponse(t.getId(), t.getGareDepartId(), nomGare(t.getGareDepartId()),
                t.getGareArriveeId(), nomGare(t.getGareArriveeId()), t.getDureeEstimeeMin());
    }

    private String nomGare(Long id) {
        return gares.findById(id).map(Gare::getNom).orElse(null);
    }

    private ApiException gareInconnue() {
        return new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", "Gare inconnue pour cette agence.");
    }

    private ApiException introuvable() {
        return new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Trajet introuvable.");
    }
}
