package cm.billetterie.service;

import cm.billetterie.dto.PageReponse;
import cm.billetterie.dto.VoyageResume;
import cm.billetterie.entity.SynonymeRecherche;
import cm.billetterie.entity.Voyage;
import cm.billetterie.entity.enums.StatutVoyage;
import cm.billetterie.entity.enums.TypeVehicule;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.SynonymeRechercheRepository;
import cm.billetterie.repository.VoyageRepository;
import cm.billetterie.service.VoyageAssembleur.Detail;
import cm.billetterie.util.Texte;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class VoyagePublicService {

    private static final ZoneId ZONE = ZoneId.of("Africa/Douala");

    private final VoyageRepository voyages;
    private final VoyageAssembleur assembleur;
    private final SynonymeRechercheRepository synonymes;

    public VoyagePublicService(VoyageRepository voyages, VoyageAssembleur assembleur,
                               SynonymeRechercheRepository synonymes) {
        this.voyages = voyages;
        this.assembleur = assembleur;
        this.synonymes = synonymes;
    }

    public PageReponse<VoyageResume> rechercher(String depart, String destination, LocalDate dateDebut,
                                                LocalDate dateFin, LocalTime heure, TypeVehicule type,
                                                Boolean clim, Boolean wifi, String q,
                                                Double latitude, Double longitude, int page, int taille) {
        LocalDateTime maintenant = LocalDateTime.now(ZONE);
        int p = Math.max(page, 0);
        int t = Math.min(Math.max(taille, 1), 100);

        // RG5 : jamais de voyage passé, annulé ou sans place
        List<Voyage> candidats = voyages.findByStatutAndPlacesRestantesGreaterThan(StatutVoyage.PROGRAMME, 0).stream()
                .filter(v -> v.getDateHeureDepart() != null && v.getDateHeureDepart().isAfter(maintenant))
                .filter(v -> dateDebut == null || !v.getDateHeureDepart().toLocalDate().isBefore(dateDebut))
                .filter(v -> dateFin == null || !v.getDateHeureDepart().toLocalDate().isAfter(dateFin))
                .toList();

        List<List<String>> motsRecherche = decouper(q);

        List<Detail> details = new ArrayList<>(assembleur.charger(candidats).stream()
                .filter(d -> Boolean.TRUE.equals(d.agence().getActif()))
                .filter(d -> vide(depart) || Texte.normaliser(d.villeDepart().getNom()).equals(Texte.normaliser(depart)))
                .filter(d -> vide(destination) || Texte.normaliser(d.villeArrivee().getNom()).equals(Texte.normaliser(destination)))
                .filter(d -> type == null || d.vehicule().getType() == type)
                .filter(d -> !Boolean.TRUE.equals(clim) || Boolean.TRUE.equals(d.vehicule().getClim()))
                .filter(d -> !Boolean.TRUE.equals(wifi) || Boolean.TRUE.equals(d.vehicule().getWifi()))
                .filter(d -> correspond(d, motsRecherche))
                .toList());

        // Tri : proximité de l'heure demandée, puis départs les plus proches, puis distance (RG21)
        Comparator<Detail> tri = (a, b) -> 0;
        if (heure != null) {
            tri = tri.thenComparingLong(d -> ecartMinutes(d.voyage().getDateHeureDepart().toLocalTime(), heure));
        }
        tri = tri.thenComparing(d -> d.voyage().getDateHeureDepart());
        if (latitude != null && longitude != null) {
            tri = tri.thenComparingDouble(d -> distanceKm(d, latitude, longitude));
        }
        details.sort(tri);

        List<VoyageResume> resumes = details.stream().map(assembleur::resume).toList();
        return PageReponse.depuisListe(resumes, p, t);
    }

    public VoyageResume detail(Long id) {
        Voyage v = voyages.findById(id).orElseThrow(this::introuvable);
        Detail d = assembleur.chargerUn(v).orElseThrow(this::introuvable);
        if (!Boolean.TRUE.equals(d.agence().getActif())) throw introuvable();
        return assembleur.resume(d);
    }

    // ----- recherche texte libre + synonymes -----

    /** Chaque mot de la requête devient un groupe d'alternatives (le mot lui-même et ses synonymes). */
    private List<List<String>> decouper(String q) {
        List<List<String>> groupes = new ArrayList<>();
        if (vide(q)) return groupes;
        Map<String, List<String>> table = new HashMap<>();
        for (SynonymeRecherche s : synonymes.findAll()) {
            table.computeIfAbsent(Texte.normaliser(s.getMot()), k -> new ArrayList<>())
                    .add(Texte.normaliser(s.getCritere()));
        }
        for (String mot : Texte.normaliser(q).split("\\s+")) {
            if (mot.isBlank()) continue;
            List<String> alternatives = new ArrayList<>();
            alternatives.add(mot);
            alternatives.addAll(table.getOrDefault(mot, List.of()));
            groupes.add(alternatives);
        }
        return groupes;
    }

    private boolean correspond(Detail d, List<List<String>> groupes) {
        if (groupes.isEmpty()) return true;
        StringBuilder sb = new StringBuilder();
        sb.append(d.agence().getNom()).append(' ').append(d.agence().getSlogan()).append(' ')
                .append(d.villeDepart().getNom()).append(' ').append(d.gareDepart().getNom()).append(' ')
                .append(d.gareDepart().getQuartier()).append(' ')
                .append(d.villeArrivee().getNom()).append(' ').append(d.gareArrivee().getNom()).append(' ')
                .append(d.gareArrivee().getQuartier()).append(' ')
                .append(d.vehicule().getType() == null ? "" : d.vehicule().getType().name()).append(' ');
        if (Boolean.TRUE.equals(d.vehicule().getClim())) sb.append("clim climatise climatisee climatisation ");
        if (Boolean.TRUE.equals(d.vehicule().getWifi())) sb.append("wifi ");
        String texte = Texte.normaliser(sb.toString());
        return groupes.stream().allMatch(g -> g.stream().anyMatch(alt -> !alt.isBlank() && texte.contains(alt)));
    }

    // ----- tri -----

    private static long ecartMinutes(LocalTime a, LocalTime b) {
        return Math.abs(Duration.between(a, b).toMinutes());
    }

    private static double distanceKm(Detail d, double lat, double lon) {
        Double la = d.gareDepart().getLatitude() != null ? d.gareDepart().getLatitude() : d.agence().getLatitude();
        Double lo = d.gareDepart().getLongitude() != null ? d.gareDepart().getLongitude() : d.agence().getLongitude();
        if (la == null || lo == null) return Double.MAX_VALUE;
        double r = 6371.0;
        double dLat = Math.toRadians(la - lat);
        double dLon = Math.toRadians(lo - lon);
        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(la))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * r * Math.asin(Math.sqrt(h));
    }

    private static boolean vide(String s) {
        return s == null || s.isBlank();
    }

    private ApiException introuvable() {
        return new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Voyage introuvable.");
    }
}
