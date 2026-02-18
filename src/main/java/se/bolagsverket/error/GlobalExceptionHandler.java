package se.bolagsverket.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Map<String, Object>> handleUserException(UserException ex) {
        HttpStatus status = mapStatus(ex.getErrorType());
        return buildResponse(ex.getMessage(), status);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(JwtException ex) {
        HttpStatus status = mapStatus(ex.getErrorType());
        return buildResponse(ex.getMessage(), status);
    }

    @ExceptionHandler(PokemonException.class)
    public ResponseEntity<Map<String, Object>> handlePokemonException(PokemonException ex) {
        HttpStatus status = mapStatus(ex.getErrorType());
        return buildResponse(ex.getMessage(), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "message", errors,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
    private HttpStatus mapStatus(ErrorType errorType) {
        return switch (errorType) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case BAD_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
            case INVALID_INPUT -> HttpStatus.BAD_REQUEST;
            case TOKEN_GENERATION_FAILED, TOKEN_PARSE_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(Map.of(
                "message", message,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}