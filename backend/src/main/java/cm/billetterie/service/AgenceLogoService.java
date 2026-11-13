package cm.billetterie.service;

import cm.billetterie.dto.LogoReponse;
import cm.billetterie.entity.Agence;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.AgenceRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AgenceLogoService {

    private static final long TAILLE_MAX = 2L * 1024 * 1024;

    private final AgenceRepository agences;
    private final Path racine;
    private final String urlBase;

    public AgenceLogoService(AgenceRepository agences,
                             @Value("${billetterie.fichiers.dossier}") String dossier,
                             @Value("${app.base-url:http://localhost:6012}") String urlBase) {
        this.agences = agences;
        this.racine = Paths.get(dossier).toAbsolutePath().normalize();
        this.urlBase = urlBase;
    }

    @Transactional
    public LogoReponse televerser(Long agenceId, MultipartFile fichier) {
        Agence a = agences.findById(agenceId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Agence introuvable."));
        if (fichier == null || fichier.isEmpty()) {
            throw validation("Aucun fichier envoyé (champ « fichier »).");
        }
        if (fichier.getSize() > TAILLE_MAX) {
            throw validation("Fichier trop volumineux (2 Mo maximum).");
        }
        try {
            byte[] octets = fichier.getBytes();
            String ext = extension(octets);
            if (ext == null) {
                throw validation("Format non accepté (jpg, png ou webp uniquement).");
            }
            Path dossierAgences = racine.resolve("agences");
            Files.createDirectories(dossierAgences);
            for (String e : List.of("jpg", "png", "webp")) {
                Files.deleteIfExists(dossierAgences.resolve("agence-" + agenceId + "." + e));
            }
            Files.write(dossierAgences.resolve("agence-" + agenceId + "." + ext), octets);
            a.setLogo("agences/agence-" + agenceId + "." + ext);
            agences.save(a);
            return new LogoReponse(urlBase + "/files/" + a.getLogo());
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "ERREUR_INTERNE",
                    "Impossible d'enregistrer le fichier.");
        }
    }

    /** Détecte le vrai format par les premiers octets (et non par le nom du fichier). */
    private String extension(byte[] b) {
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) return "jpg";
        if (b.length >= 4 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') return "png";
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') return "webp";
        return null;
    }

    private ApiException validation(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", message);
    }
}
