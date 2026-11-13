package cm.billetterie.dto;

import cm.billetterie.entity.enums.RoleCompte;

public record CompteReponse(Long id, String telephone, String email, RoleCompte role, Long agenceId, boolean actif) {
}
