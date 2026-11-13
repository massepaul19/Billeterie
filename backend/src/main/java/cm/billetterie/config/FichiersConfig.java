package cm.billetterie.config;

import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Sert le dossier des images sur GET /files/** (public, sans jeton). */
@Configuration
public class FichiersConfig implements WebMvcConfigurer {

    private final String dossier;

    public FichiersConfig(@Value("${billetterie.fichiers.dossier}") String dossier) {
        this.dossier = dossier;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String emplacement = Paths.get(dossier).toAbsolutePath().normalize().toUri().toString();
        if (!emplacement.endsWith("/")) emplacement += "/";
        registry.addResourceHandler("/files/**").addResourceLocations(emplacement);
    }
}
