package cm.billetterie.dto;

import java.time.LocalDate;

public record ModeleReponse(Long id, Long trajetId, Long vehiculeId, Integer prix, String joursSemaine,
                            String heure, LocalDate dateDebut, LocalDate dateFin, boolean actif) {
}
