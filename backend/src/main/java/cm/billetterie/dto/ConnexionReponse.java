package cm.billetterie.dto;

import cm.billetterie.entity.enums.RoleCompte;

public record ConnexionReponse(String token, RoleCompte role, Long agenceId) {
}
