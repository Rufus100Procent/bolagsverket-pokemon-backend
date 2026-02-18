package se.bolagsverket.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;


public final class TestValidators {

    private static final ValidatorFactory FACTORY =
            Validation.buildDefaultValidatorFactory();

    private static final Validator VALIDATOR =
            FACTORY.getValidator();

    private TestValidators() {
    }

    public static Validator validator() {
        return VALIDATOR;
    }

    static {
        Runtime.getRuntime().addShutdownHook(
                new Thread(FACTORY::close)
        );
    }
}
