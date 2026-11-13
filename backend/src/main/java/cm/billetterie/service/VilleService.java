package cm.billetterie.service;

import cm.billetterie.dto.VilleDto;
import cm.billetterie.repository.VilleRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VilleService {

    private final VilleRepository villes;

    public VilleService(VilleRepository villes) {
        this.villes = villes;
    }

    public List<VilleDto> rechercher(String q) {
        var liste = (q == null || q.isBlank())
                ? villes.findAllByOrderByNom()
                : villes.findByNomContainingIgnoreCaseOrderByNom(q.trim());
        return liste.stream().limit(50).map(v -> new VilleDto(v.getId(), v.getNom())).toList();
    }
}
