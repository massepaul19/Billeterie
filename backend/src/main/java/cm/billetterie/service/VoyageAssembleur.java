package cm.billetterie.service;

import cm.billetterie.dto.VoyageResume;
import cm.billetterie.entity.*;
import cm.billetterie.repository.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Charge d'un coup trajet, gares, villes, agence et véhicule des voyages (les entités n'ont pas de relations JPA). */
@Component
public class VoyageAssembleur {

    public record Detail(Voyage voyage, Agence agence, Trajet trajet, Gare gareDepart, Ville villeDepart,
                         Gare gareArrivee, Ville villeArrivee, Vehicule vehicule) {}

    private final TrajetRepository trajets;
    private final GareRepository gares;
    private final VilleRepository villes;
    private final AgenceRepository agences;
    private final VehiculeRepository vehicules;
    private final FichierStorageService fichiers;

    public VoyageAssembleur(TrajetRepository trajets, GareRepository gares, VilleRepository villes,
                            AgenceRepository agences, VehiculeRepository vehicules, FichierStorageService fichiers) {
        this.trajets = trajets;
        this.gares = gares;
        this.villes = villes;
        this.agences = agences;
        this.vehicules = vehicules;
        this.fichiers = fichiers;
    }

    public List<Detail> charger(Collection<Voyage> voyages) {
        if (voyages.isEmpty()) return List.of();
        Map<Long, Trajet> mapTrajets = indexer(trajets.findAllById(ids(voyages, Voyage::getTrajetId)), Trajet::getId);
        Set<Long> idsGares = new HashSet<>();
        mapTrajets.values().forEach(t -> {
            idsGares.add(t.getGareDepartId());
            idsGares.add(t.getGareArriveeId());
        });
        idsGares.remove(null);
        Map<Long, Gare> mapGares = indexer(gares.findAllById(idsGares), Gare::getId);
        Set<Long> idsVilles = mapGares.values().stream().map(Gare::getVilleId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Ville> mapVilles = indexer(villes.findAllById(idsVilles), Ville::getId);
        Map<Long, Agence> mapAgences = indexer(agences.findAllById(ids(voyages, Voyage::getAgenceId)), Agence::getId);
        Map<Long, Vehicule> mapVehicules = indexer(vehicules.findAllById(ids(voyages, Voyage::getVehiculeId)), Vehicule::getId);

        List<Detail> resultat = new ArrayList<>();
        for (Voyage v : voyages) {
            Trajet t = mapTrajets.get(v.getTrajetId());
            if (t == null) continue;
            Gare gd = mapGares.get(t.getGareDepartId());
            Gare ga = mapGares.get(t.getGareArriveeId());
            if (gd == null || ga == null) continue;
            Ville vd = mapVilles.get(gd.getVilleId());
            Ville va = mapVilles.get(ga.getVilleId());
            Agence a = mapAgences.get(v.getAgenceId());
            Vehicule ve = mapVehicules.get(v.getVehiculeId());
            if (vd == null || va == null || a == null || ve == null) continue;
            resultat.add(new Detail(v, a, t, gd, vd, ga, va, ve));
        }
        return resultat;
    }

    public Optional<Detail> chargerUn(Voyage voyage) {
        return charger(List.of(voyage)).stream().findFirst();
    }

    public VoyageResume resume(Detail d) {
        Voyage v = d.voyage();
        return new VoyageResume(
                v.getId(),
                new VoyageResume.AgenceInfo(d.agence().getId(), d.agence().getNom(),
                        fichiers.urlComplete(d.agence().getLogo()), d.agence().getSlogan()),
                new VoyageResume.LieuInfo(d.villeDepart().getNom(), d.gareDepart().getNom(), d.gareDepart().getQuartier()),
                new VoyageResume.LieuInfo(d.villeArrivee().getNom(), d.gareArrivee().getNom(), d.gareArrivee().getQuartier()),
                v.getDateHeureDepart(),
                d.trajet().getDureeEstimeeMin(),
                v.getPrix(),
                new VoyageResume.VehiculeInfo(
                        d.vehicule().getType() == null ? null : d.vehicule().getType().name(),
                        d.vehicule().getClim(), d.vehicule().getWifi(),
                        fichiers.urlComplete(d.vehicule().getImage())));
    }

    private static <T> Set<Long> ids(Collection<T> items, Function<T, Long> f) {
        return items.stream().map(f).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private static <T> Map<Long, T> indexer(List<T> items, Function<T, Long> f) {
        return items.stream().collect(Collectors.toMap(f, Function.identity(), (a, b) -> a));
    }
}
