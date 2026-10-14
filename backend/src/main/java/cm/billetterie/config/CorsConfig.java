package cm.billetterie.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Développement : tout est permis (Flutter web). TODO (Paulo, déploiement) : restreindre au domaine.
        registry.addMapping("/api/**").allowedOriginPatterns("*").allowedMethods("*").allowedHeaders("*");
    }
}
