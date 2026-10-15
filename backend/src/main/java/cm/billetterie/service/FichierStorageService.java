package cm.billetterie.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Stockage des images (logos d'agences, photos de véhicules) - Back A (Balla), Sprint 1.
 *
 * À coder : enregistrer le fichier reçu (jpg, png, webp, 2 Mo max, sinon 400 VALIDATION) dans le dossier
 * billetterie.fichiers.dossier. La base ne garde que le chemin relatif (ex. agences/3f2a9c.png) ;
 * le front reçoit l'URL complète (RG20).
 */
@Service
public class FichierStorageService {

    /** Enregistre le fichier dans sousDossier (ex. "agences", "vehicules") et renvoie le chemin relatif. */
    public String enregistrer(MultipartFile fichier, String sousDossier) {
        throw new UnsupportedOperationException("TODO (Back A) : enregistrer le fichier");
    }

    /** Transforme le chemin relatif en URL complète (billetterie.public-url + /files/ + chemin), ou null. */
    public String urlComplete(String cheminRelatif) {
        throw new UnsupportedOperationException("TODO (Back A) : construire l'URL complète");
    }
}
