package cm.billetterie.security;

import cm.billetterie.entity.enums.RoleCompte;
import cm.billetterie.repository.AgenceRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Protège /api/admin/** (SUPER_ADMIN), /api/agence/** (ADMIN_AGENCE) et /api/auth/mot-de-passe. */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final AgenceRepository agences;

    public JwtFilter(JwtService jwt, AgenceRepository agences) {
        this.jwt = jwt;
        this.agences = agences;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true; // préflight CORS
        String p = req.getRequestURI();
        return !(p.startsWith("/api/admin/") || p.startsWith("/api/agence/")
                || p.equals("/api/auth/mot-de-passe"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String entete = req.getHeader("Authorization");
        if (entete == null || !entete.startsWith("Bearer ")) {
            erreur(res, 401, "NON_AUTHENTIFIE", "Authentification requise.");
            return;
        }
        Optional<AuthPrincipal> auth = jwt.verifier(entete.substring(7).trim());
        if (auth.isEmpty()) {
            erreur(res, 401, "NON_AUTHENTIFIE", "Jeton invalide ou expiré.");
            return;
        }
        AuthPrincipal a = auth.get();
        String p = req.getRequestURI();

        if (p.startsWith("/api/admin/") && a.role() != RoleCompte.SUPER_ADMIN) {
            erreur(res, 403, "INTERDIT", "Accès réservé au super administrateur.");
            return;
        }
        if (p.startsWith("/api/agence/")) {
            if (a.role() != RoleCompte.ADMIN_AGENCE || a.agenceId() == null) {
                erreur(res, 403, "INTERDIT", "Accès réservé aux administrateurs d'agence.");
                return;
            }
            // RG16 : une agence désactivée perd l'accès même avec un jeton encore valide
            boolean active = agences.findById(a.agenceId())
                    .map(x -> Boolean.TRUE.equals(x.getActif())).orElse(false);
            if (!active) {
                erreur(res, 403, "INTERDIT", "Votre agence est désactivée.");
                return;
            }
        }
        req.setAttribute(AuthPrincipal.ATTRIBUT, a);
        chain.doFilter(req, res);
    }

    private static void erreur(HttpServletResponse res, int statut, String code, String message) throws IOException {
        res.setStatus(statut);
        res.setContentType("application/json;charset=UTF-8");
        res.getOutputStream().write(
                ("{\"code\":\"" + code + "\",\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8));
    }
}
