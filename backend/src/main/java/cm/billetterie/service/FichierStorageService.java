package cm.billetterie.service;

import cm.billetterie.exception.ApiException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Stockage des images (logos d'agences, photos de véhicules).
 * La base ne garde que le chemin relatif (ex. agences/3f2a9c.png) ; le front reçoit l'URL complète (RG20).
 */
@Service
public class FichierStorageService {

    private static final long TAILLE_MAX = 2L * 1024 * 1024;
    private static final Set<String> EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final Path racine;
    private final String urlPublique;

    public FichierStorageService(@Value("${billetterie.fichiers.dossier}") String dossier,
                                 @Value("${billetterie.public-url}") String urlPublique) {
        this.racine = Paths.get(dossier).toAbsolutePath().normalize();
        this.urlPublique = urlPublique.endsWith("/") ? urlPublique.substring(0, urlPublique.length() - 1) : urlPublique;
    }

    /** Enregistre le fichier dans sousDossier (ex. "agences", "vehicules") et renvoie le chemin relatif. */
    public String enregistrer(MultipartFile fichier, String sousDossier) {
        if (fichier == null || fichier.isEmpty()) {
            throw validation("Aucun fichier reçu (champ « fichier »).");
        }
        if (fichier.getSize() > TAILLE_MAX) {
            throw validation("Fichier trop volumineux (2 Mo maximum).");
        }
        String extension = extension(fichier.getOriginalFilename());
        if (!EXTENSIONS.contains(extension)) {
            throw validation("Format non accepté (jpg, png ou webp).");
        }
        if (!sousDossier.matches("[a-z0-9_-]+")) {
            throw new IllegalArgumentException("Sous-dossier invalide : " + sousDossier);
        }
        String nom = UUID.randomUUID().toString().replace("-", "").substring(0, 12) + "." + extension;
        Path dossier = racine.resolve(sousDossier);
        try {
            Files.createDirectories(dossier);
            try (InputStream in = fichier.getInputStream()) {
                Files.copy(in, dossier.resolve(nom), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "ERREUR_INTERNE",
                    "Impossible d'enregistrer le fichier.");
        }
        return sousDossier + "/" + nom;
    }

    /** Transforme le chemin relatif en URL complète (billetterie.public-url + /files/ + chemin), ou null. */
    public String urlComplete(String cheminRelatif) {
        if (cheminRelatif == null || cheminRelatif.isBlank()) return null;
        return urlPublique + "/files/" + cheminRelatif;
    }

    /** Supprime un fichier (ignore les erreurs et tout chemin hors du dossier de stockage). */
    public void supprimer(String cheminRelatif) {
        if (cheminRelatif == null || cheminRelatif.isBlank()) return;
        try {
            Path cible = racine.resolve(cheminRelatif).normalize();
            if (cible.startsWith(racine)) Files.deleteIfExists(cible);
        } catch (IOException | RuntimeException ignore) {
            // pas bloquant
        }
    }

    private static String extension(String nomOriginal) {
        if (nomOriginal == null || !nomOriginal.contains(".")) return "";
        return nomOriginal.substring(nomOriginal.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private static ApiException validation(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", message);
    }
}
