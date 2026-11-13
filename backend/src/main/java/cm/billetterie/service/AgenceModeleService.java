package cm.billetterie.service;

import cm.billetterie.dto.ModeleReponse;
import cm.billetterie.dto.ModeleRequete;
import cm.billetterie.entity.ModeleVoyage;
import cm.billetterie.entity.Vehicule;
import cm.billetterie.entity.Voyage;
import cm.billetterie.entity.enums.StatutVoyage;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.ModeleAgenceRepository;
import cm.billetterie.repository.TrajetRepository;
import cm.billetterie.repository.VehiculeRepository;
import cm.billetterie.repository.VoyageAgenceRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Modèles de voyages récurrents : génèrent les voyages des 60 prochains jours (et chaque jour ensuite). */
@Service
public class AgenceModeleService {

    private static final ZoneId ZONE = ZoneId.of("Africa/Douala");
    private static final int HORIZON_JOURS = 60;

    private final ModeleAgenceRepository modeles;
    private final TrajetRepository trajets;
    private final VehiculeRepository vehicules;
    private final VoyageAgenceRepository voyages;

    public AgenceModeleService(ModeleAgenceRepository modeles, TrajetRepository trajets,
                               VehiculeRepository vehicules, VoyageAgenceRepository voyages) {
        this.modeles = modeles;
        this.trajets = trajets;
        this.vehicules = vehicules;
        this.voyages = voyages;
    }

    public List<ModeleReponse> lister(Long agenceId) {
        return modeles.findByAgenceIdOrderById(agenceId).stream().map(this::vers).toList();
    }

    @Transactional
    public ModeleReponse creer(Long agenceId, ModeleRequete r) {
        ModeleVoyage m = new ModeleVoyage();
        m.setActif(true);
        m = modeles.save(remplir(m, agenceId, r));
        generer(m);
        return vers(m);
    }

    @Transactional
    public ModeleReponse modifier(Long agenceId, Long id, ModeleRequete r) {
        ModeleVoyage m = modeles.findByIdAndAgenceId(id, agenceId).orElseThrow(this::introuvable);
        m = modeles.save(remplir(m, agenceId, r));
        generer(m); // les voyages déjà générés ne sont pas modifiés
        return vers(m);
    }

    @Transactional
    public void supprimer(Long agenceId, Long id) {
        ModeleVoyage m = modeles.findByIdAndAgenceId(id, agenceId).orElseThrow(this::introuvable);
        List<Voyage> generes = voyages.findByModeleId(id);
        generes.forEach(v -> v.setModeleId(null)); // les voyages déjà créés restent
        voyages.saveAll(generes);
        modeles.delete(m);
    }

    /** Appelé chaque jour par le planificateur : prolonge l'horizon de tous les modèles actifs. */
    @Transactional
    public int genererPourTous() {
        int total = 0;
        for (ModeleVoyage m : modeles.findByActifTrue()) total += generer(m);
        return total;
    }

    private int generer(ModeleVoyage m) {
        Vehicule v = vehicules.findById(m.getVehiculeId()).orElse(null);
        if (v == null) return 0;
        Set<Integer> jours = jours(m.getJoursSemaine());
        LocalTime heure = LocalTime.parse(m.getHeure());
        LocalDateTime maintenant = LocalDateTime.now(ZONE);
        LocalDate aujourdhui = maintenant.toLocalDate();
        LocalDate debut = m.getDateDebut().isAfter(aujourdhui) ? m.getDateDebut() : aujourdhui;
        LocalDate limite = aujourdhui.plusDays(HORIZON_JOURS);
        LocalDate fin = m.getDateFin().isBefore(limite) ? m.getDateFin() : limite;
        int crees = 0;
        for (LocalDate d = debut; !d.isAfter(fin); d = d.plusDays(1)) {
            if (!jours.contains(d.getDayOfWeek().getValue())) continue;
            LocalDateTime depart = d.atTime(heure);
            if (!depart.isAfter(maintenant)) continue;
            if (voyages.existsByModeleIdAndDateHeureDepart(m.getId(), depart)) continue;
            Voyage x = new Voyage();
            x.setAgenceId(m.getAgenceId());
            x.setTrajetId(m.getTrajetId());
            x.setVehiculeId(m.getVehiculeId());
            x.setModeleId(m.getId());
            x.setDateHeureDepart(depart);
            x.setPrix(m.getPrix());
            x.setPlacesRestantes(v.getNbPlaces());
            x.setStatut(StatutVoyage.PROGRAMME);
            voyages.save(x);
            crees++;
        }
        return crees;
    }

    private ModeleVoyage remplir(ModeleVoyage m, Long agenceId, ModeleRequete r) {
        trajets.findByIdAndAgenceId(r.trajetId(), agenceId).orElseThrow(() -> validation("Trajet inconnu pour cette agence."));
        vehicules.findByIdAndAgenceId(r.vehiculeId(), agenceId).orElseThrow(() -> validation("Véhicule inconnu pour cette agence."));
        LocalTime heure;
        try {
            heure = LocalTime.parse(r.heure().trim());
        } catch (DateTimeParseException e) {
            throw validation("Heure invalide (format HH:mm).");
        }
        TreeSet<Integer> jours = new TreeSet<>();
        Matcher mt = Pattern.compile("[1-7]").matcher(r.joursSemaine());
        while (mt.find()) jours.add(Integer.parseInt(mt.group()));
        if (jours.isEmpty()) throw validation("Indiquez au moins un jour (1 = lundi ... 7 = dimanche).");
        if (r.dateFin().isBefore(r.dateDebut())) throw validation("La date de fin précède la date de début.");
        m.setAgenceId(agenceId);
        m.setTrajetId(r.trajetId());
        m.setVehiculeId(r.vehiculeId());
        m.setPrix(r.prix());
        m.setJoursSemaine(jours.stream().map(String::valueOf).collect(Collectors.joining(",")));
        m.setHeure(heure.toString());
        m.setDateDebut(r.dateDebut());
        m.setDateFin(r.dateFin());
        return m;
    }

    private static Set<Integer> jours(String texte) {
        Set<Integer> s = new TreeSet<>();
        Matcher mt = Pattern.compile("[1-7]").matcher(texte == null ? "" : texte);
        while (mt.find()) s.add(Integer.parseInt(mt.group()));
        return s;
    }

    private ModeleReponse vers(ModeleVoyage m) {
        return new ModeleReponse(m.getId(), m.getTrajetId(), m.getVehiculeId(), m.getPrix(), m.getJoursSemaine(),
                m.getHeure(), m.getDateDebut(), m.getDateFin(), Boolean.TRUE.equals(m.getActif()));
    }

    private ApiException introuvable() {
        return new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Modèle introuvable.");
    }

    private static ApiException validation(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", message);
    }
}
