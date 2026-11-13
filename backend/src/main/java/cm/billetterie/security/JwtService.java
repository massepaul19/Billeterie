package cm.billetterie.security;

import cm.billetterie.entity.CompteAdmin;
import cm.billetterie.entity.enums.RoleCompte;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey cle;
    private final long dureeHeures;

    public JwtService(@Value("${billetterie.jwt.secret:}") String secret,
                      @Value("${billetterie.jwt.duree-heures:12}") long dureeHeures) {
        this.dureeHeures = dureeHeures;
        if (secret != null && secret.getBytes(StandardCharsets.UTF_8).length >= 32) {
            this.cle = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        } else {
            byte[] alea = new byte[48];
            new SecureRandom().nextBytes(alea);
            this.cle = Keys.hmacShaKeyFor(alea);
            log.warn("billetterie.jwt.secret absent ou trop court (32 caractères minimum) : "
                    + "clé aléatoire générée, les jetons seront invalides après un redémarrage.");
        }
    }

    public String generer(CompteAdmin c) {
        Instant maintenant = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(c.getId()))
                .claim("role", c.getRole().name())
                .claim("agenceId", c.getAgenceId())
                .issuedAt(Date.from(maintenant))
                .expiration(Date.from(maintenant.plus(dureeHeures, ChronoUnit.HOURS)))
                .signWith(cle)
                .compact();
    }

    public Optional<AuthPrincipal> verifier(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(cle).build().parseSignedClaims(token).getPayload();
            Number agence = claims.get("agenceId", Number.class);
            return Optional.of(new AuthPrincipal(
                    Long.parseLong(claims.getSubject()),
                    RoleCompte.valueOf(claims.get("role", String.class)),
                    agence == null ? null : agence.longValue()));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
