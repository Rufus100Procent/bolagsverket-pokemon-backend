package se.bolagsverket.error;

public class PokemonException extends RuntimeException {

    private final ErrorType errorType;

    public PokemonException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}