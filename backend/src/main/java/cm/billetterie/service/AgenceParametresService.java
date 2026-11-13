package cm.billetterie.service;

import cm.billetterie.dto.RappelsDto;
import cm.billetterie.entity.Agence;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.AgenceRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgenceParametresService {

    private final AgenceRepository agences;

    public AgenceParametresService(AgenceRepository agences) {
        this.agences = agences;
    }

    public RappelsDto lireRappels(Long agenceId) {
        return new RappelsDto(extraire(trouver(agenceId).getDelaisRappelHeures()));
    }

    @Transactional
    public RappelsDto modifierRappels(Long agenceId, RappelsDto r) {
        Agence a = trouver(agenceId);
        List<Integer> delais = r.delaisHeures().stream().distinct().sorted(Comparator.reverseOrder()).toList();
        a.setDelaisRappelHeures(delais.stream().map(String::valueOf).collect(Collectors.joining(",")));
        agences.save(a);
        return new RappelsDto(delais);
    }

    private List<Integer> extraire(String texte) {
        List<Integer> liste = new ArrayList<>();
        if (texte != null) {
            Matcher m = Pattern.compile("\\d+").matcher(texte);
            while (m.find()) {
                try { liste.add(Integer.parseInt(m.group())); } catch (NumberFormatException ignore) { }
            }
        }
        return liste;
    }

    private Agence trouver(Long agenceId) {
        return agences.findById(agenceId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Agence introuvable."));
    }
}
