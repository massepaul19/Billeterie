package cm.billetterie.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErreurApi> handle(ApiException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErreurApi(e.getCode(), e.getMessage()));
    }

    // TODO (Back A) : gérer MethodArgumentNotValidException -> 400 VALIDATION,
    //                 AccessDeniedException -> 403 INTERDIT, Exception générique -> 500
}
