package cm.billetterie.exception;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Erreurs métier levées volontairement par les services. */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErreurApi> handle(ApiException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErreurApi(e.getCode(), e.getMessage()));
    }

    /** Corps JSON invalide : champs manquants ou mal formés (@Valid). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurApi> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + " : " + err.getDefaultMessage())
                .collect(Collectors.joining(" ; "));
        if (message.isBlank()) message = "Données invalides.";
        return ResponseEntity.badRequest().body(new ErreurApi("VALIDATION", message));
    }

    /** JSON illisible, paramètre manquant ou de mauvais type. */
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErreurApi> handleRequeteMalFormee(Exception e) {
        return ResponseEntity.badRequest()
                .body(new ErreurApi("VALIDATION", "Requête mal formée ou paramètre invalide."));
    }

    /** Dernier filet de sécurité : jamais de stack trace envoyée au client. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurApi> handleGenerique(Exception e) {
        if (e instanceof ErrorResponse er) {
            HttpStatusCode status = er.getStatusCode();
            String code = status.value() == 404 ? "INTROUVABLE" : "REQUETE_INVALIDE";
            return ResponseEntity.status(status).body(new ErreurApi(code, "Requête non prise en charge."));
        }
        log.error("Erreur interne non prévue", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErreurApi("ERREUR_INTERNE", "Une erreur interne est survenue."));
    }
}
