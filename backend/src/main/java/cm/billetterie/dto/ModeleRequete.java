package cm.billetterie.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** joursSemaine : numéros ISO séparés par des virgules (1 = lundi ... 7 = dimanche), ex. "1,3,5". heure : "HH:mm". */
public record ModeleRequete(@NotNull Long trajetId, @NotNull Long vehiculeId, @NotNull @Min(0) Integer prix,
                            @NotBlank String joursSemaine, @NotBlank String heure,
                            @NotNull LocalDate dateDebut, @NotNull LocalDate dateFin) {
}
