package cm.billetterie.security;

import cm.billetterie.entity.enums.RoleCompte;

/** Identité tirée du jeton. L'agenceId vient TOUJOURS d'ici, jamais du corps de la requête. */
public record AuthPrincipal(Long compteId, RoleCompte role, Long agenceId) {

    public static final String ATTRIBUT = "auth";
}
