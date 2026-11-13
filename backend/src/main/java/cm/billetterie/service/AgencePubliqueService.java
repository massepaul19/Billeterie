package cm.billetterie.service;

import cm.billetterie.dto.AgenceCarteDto;
import cm.billetterie.entity.Agence;
import cm.billetterie.entity.Ville;
import cm.billetterie.repository.AgenceRepository;
import cm.billetterie.repository.VilleRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AgencePubliqueService {

    private final AgenceRepository agences;
    private final VilleRepository villes;
    private final FichierStorageService fichiers;

    public AgencePubliqueService(AgenceRepository agences, VilleRepository villes, FichierStorageService fichiers) {
        this.agences = agences;
        this.villes = villes;
        this.fichiers = fichiers;
    }

    /** Agences actives uniquement (RG16). */
    public List<AgenceCarteDto> lister() {
        List<Agence> actives = agences.findByActifTrueOrderByNom();
        Map<Long, Ville> mapVilles = villes.findAll().stream()
                .collect(Collectors.toMap(Ville::getId, Function.identity(), (a, b) -> a));
        return actives.stream().map(a -> {
            Ville v = mapVilles.get(a.getVilleId());
            return new AgenceCarteDto(a.getId(), a.getNom(), fichiers.urlComplete(a.getLogo()), a.getSlogan(),
                    v == null ? null : v.getNom(), a.getLatitude(), a.getLongitude());
        }).toList();
    }
}
