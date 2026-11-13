package cm.billetterie.dto;

import cm.billetterie.entity.enums.StatutBillet;
import java.time.LocalDateTime;

/** Billet vu par l'agence (vérification et recherche). */
public record BilletAgenceReponse(String code, StatutBillet statut, LocalDateTime valideJusquA,
                                  ClientInfo client, VoyageInfo voyage) {

    public record ClientInfo(String nom, String prenom, String telephone) {}

    public record VoyageInfo(Long id, String gareDepart, String gareArrivee, LocalDateTime dateHeureDepart) {}
}
