package cm.billetterie.service;

import cm.billetterie.dto.CommissionReponse;
import cm.billetterie.entity.Agence;
import cm.billetterie.entity.ReleveCommission;
import cm.billetterie.entity.enums.StatutFacturation;
import cm.billetterie.entity.enums.StatutReservation;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.AgenceRepository;
import cm.billetterie.repository.ReleveCommissionRepository;
import cm.billetterie.repository.ReservationAgenceRepository;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuperAdminCommissionService {

    private static final ZoneId ZONE = ZoneId.of("Africa/Douala");

    private final ReservationAgenceRepository reservations;
    private final ReleveCommissionRepository releves;
    private final AgenceRepository agences;

    public SuperAdminCommissionService(ReservationAgenceRepository reservations,
                                       ReleveCommissionRepository releves, AgenceRepository agences) {
        this.reservations = reservations;
        this.releves = releves;
        this.agences = agences;
    }

    @Transactional
    public List<CommissionReponse> lister(String mois) {
        YearMonth ym;
        try {
            ym = (mois == null || mois.isBlank()) ? YearMonth.now(ZONE) : YearMonth.parse(mois.trim());
        } catch (DateTimeParseException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", "Mois invalide (format AAAA-MM).");
        }
        final String cle = ym.toString();
        LocalDateTime debut = ym.atDay(1).atStartOfDay();
        LocalDateTime fin = ym.plusMonths(1).atDay(1).atStartOfDay();

        List<CommissionReponse> resultat = new ArrayList<>();
        for (Object[] ligne : reservations.totauxParAgence(StatutReservation.PAYEE, debut, fin)) {
            Long agenceId = ((Number) ligne[0]).longValue();
            int total = ligne[1] == null ? 0 : ((Number) ligne[1]).intValue();
            int commission = ligne[2] == null ? 0 : ((Number) ligne[2]).intValue();
            ReleveCommission rel = releves.findByAgenceIdAndMois(agenceId, cle).orElseGet(() -> {
                ReleveCommission n = new ReleveCommission();
                n.setAgenceId(agenceId);
                n.setMois(cle);
                n.setStatutFacturation(StatutFacturation.A_FACTURER);
                return n;
            });
            rel.setTotalVentes(total);
            rel.setCommission(commission);
            rel = releves.save(rel);
            String nom = agences.findById(agenceId).map(Agence::getNom).orElse("Agence " + agenceId);
            resultat.add(new CommissionReponse(agenceId, nom, total, commission, rel.getStatutFacturation()));
        }
        resultat.sort(Comparator.comparing(CommissionReponse::agenceNom, String.CASE_INSENSITIVE_ORDER));
        return resultat;
    }
}
