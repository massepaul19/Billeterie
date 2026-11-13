package cm.billetterie.service;

import cm.billetterie.dto.VehiculeReponse;
import cm.billetterie.dto.VehiculeRequete;
import cm.billetterie.entity.Vehicule;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.ModeleLiaisonRepository;
import cm.billetterie.repository.VehiculeRepository;
import cm.billetterie.repository.VoyageLiaisonRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgenceVehiculeService {

    private final VehiculeRepository vehicules;
    private final VoyageLiaisonRepository voyages;
    private final ModeleLiaisonRepository modeles;
    private final FichierStorageService fichiers;

    public AgenceVehiculeService(VehiculeRepository vehicules, VoyageLiaisonRepository voyages,
                                 ModeleLiaisonRepository modeles, FichierStorageService fichiers) {
        this.vehicules = vehicules;
        this.voyages = voyages;
        this.modeles = modeles;
        this.fichiers = fichiers;
    }

    public List<VehiculeReponse> lister(Long agenceId) {
        return vehicules.findByAgenceIdOrderByImmatriculation(agenceId).stream().map(this::vers).toList();
    }

    @Transactional
    public VehiculeReponse creer(Long agenceId, VehiculeRequete r) {
        Vehicule v = new Vehicule();
        v.setAgenceId(agenceId);
        return vers(vehicules.save(remplir(v, r)));
    }

    @Transactional
    public VehiculeReponse modifier(Long agenceId, Long id, VehiculeRequete r) {
        Vehicule v = vehicules.findByIdAndAgenceId(id, agenceId).orElseThrow(this::introuvable);
        return vers(vehicules.save(remplir(v, r)));
    }

    @Transactional
    public void supprimer(Long agenceId, Long id) {
        Vehicule v = vehicules.findByIdAndAgenceId(id, agenceId).orElseThrow(this::introuvable);
        if (voyages.existsByVehiculeId(id) || modeles.existsByVehiculeId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "CONFLIT",
                    "Ce véhicule est utilisé par un voyage ou un modèle de voyage.");
        }
        vehicules.delete(v);
        fichiers.supprimer(v.getImage());
    }

    private Vehicule remplir(Vehicule v, VehiculeRequete r) {
        String immat = r.immatriculation().trim();
        vehicules.findByAgenceIdAndImmatriculationIgnoreCase(v.getAgenceId(), immat).ifPresent(autre -> {
            if (!autre.getId().equals(v.getId())) {
                throw new ApiException(HttpStatus.CONFLICT, "CONFLIT", "Cette immatriculation existe déjà.");
            }
        });
        v.setImmatriculation(immat);
        v.setType(r.type());
        v.setNbPlaces(r.nbPlaces());
        v.setClim(Boolean.TRUE.equals(r.clim()));
        v.setWifi(Boolean.TRUE.equals(r.wifi()));
        return v;
    }

    private VehiculeReponse vers(Vehicule v) {
        return new VehiculeReponse(v.getId(), v.getImmatriculation(), v.getType(), v.getNbPlaces(),
                Boolean.TRUE.equals(v.getClim()), Boolean.TRUE.equals(v.getWifi()),
                fichiers.urlComplete(v.getImage()));
    }

    private ApiException introuvable() {
        return new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Véhicule introuvable.");
    }
}
