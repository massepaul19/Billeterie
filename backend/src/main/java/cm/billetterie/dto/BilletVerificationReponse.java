package cm.billetterie.dto;

public record BilletVerificationReponse(String statut, boolean accepte, ClientInfo client, VoyageResume voyage) {

    public record ClientInfo(String nom, String prenom, String telephone) {
    }
}
