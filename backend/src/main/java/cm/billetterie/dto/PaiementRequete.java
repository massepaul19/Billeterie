package cm.billetterie.dto;

import cm.billetterie.entity.enums.FournisseurPaiement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaiementRequete(
        @NotNull Long reservationId,
        @NotNull FournisseurPaiement fournisseur,
        String operateur,
        @NotBlank String telephonePaiement) {}
