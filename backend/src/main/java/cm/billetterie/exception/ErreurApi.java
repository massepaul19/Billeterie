package cm.billetterie.exception;

/** Format d'erreur unique de l'API : { "code": "...", "message": "..." } */
public record ErreurApi(String code, String message) {}
