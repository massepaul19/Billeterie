package cm.billetterie.service;

import cm.billetterie.dto.ImageReponse;
import cm.billetterie.entity.Vehicule;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.VehiculeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VehiculeImageService {

    private final VehiculeRepository vehicules;
    private final FichierStorageService fichiers;

    public VehiculeImageService(VehiculeRepository vehicules, FichierStorageService fichiers) {
        this.vehicules = vehicules;
        this.fichiers = fichiers;
    }

    @Transactional
    public ImageReponse televerser(Long agenceId, Long vehiculeId, MultipartFile fichier) {
        Vehicule v = vehicules.findByIdAndAgenceId(vehiculeId, agenceId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Véhicule introuvable."));
        String chemin = fichiers.enregistrer(fichier, "vehicules");
        String ancien = v.getImage();
        v.setImage(chemin);
        vehicules.save(v);
        fichiers.supprimer(ancien);
        return new ImageReponse(fichiers.urlComplete(chemin));
    }

    @Transactional
    public ImageReponse definir(Long agenceId, Long vehiculeId, MultipartFile fichier) {
        return televerser(agenceId, vehiculeId, fichier);
    }
}
