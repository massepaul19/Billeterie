package cm.billetterie.service;

import cm.billetterie.dto.CommissionTauxDto;
import cm.billetterie.entity.Parametre;
import cm.billetterie.repository.ParametreRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logique métier du module SuperAdminParametre (règles : docs/REGLES_GESTION.md).
 * Propriétaire : Back A (Balla) - Sprint 2
 */
@Service
public class SuperAdminParametreService {

    private static final String CLE = "commission_taux";
    private static final double TAUX_DEFAUT = 0.007;

    private final ParametreRepository parametres;

    public SuperAdminParametreService(ParametreRepository parametres) {
        this.parametres = parametres;
    }

    public CommissionTauxDto lireCommission() {
        double taux = parametres.findById(CLE)
                .map(p -> {
                    try { return Double.parseDouble(p.getValeur()); }
                    catch (NumberFormatException e) { return TAUX_DEFAUT; }
                })
                .orElse(TAUX_DEFAUT);
        return new CommissionTauxDto(taux);
    }

    @Transactional
    public CommissionTauxDto modifierCommission(CommissionTauxDto r) {
        Parametre p = parametres.findById(CLE).orElseGet(() -> {
            Parametre n = new Parametre();
            n.setCle(CLE);
            return n;
        });
        p.setValeur(String.valueOf(r.taux()));
        p.setModifieLe(LocalDateTime.now());
        parametres.save(p);
        return new CommissionTauxDto(r.taux());
    }
}
