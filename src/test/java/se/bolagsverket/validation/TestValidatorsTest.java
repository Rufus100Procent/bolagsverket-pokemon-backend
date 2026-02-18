package se.bolagsverket.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class TestValidatorsTest {

    @Test
    void validator_is_not_null() {
        Validator validator = TestValidators.validator();

        assertNotNull(validator);
    }

    @Test
    void validator_is_reusable_singleton() {
        Validator v1 = TestValidators.validator();
        Validator v2 = TestValidators.validator();

        assertSame(v1, v2);
    }

    @Test
    void validator_actually_validates_constraints() {

        class Dummy {
            @SuppressWarnings("unused")
            @NotNull
            String value;
        }

        Dummy dto = new Dummy();

        Set<ConstraintViolation<Dummy>> violations =
                TestValidators.validator().validate(dto);

        assertFalse(violations.isEmpty());
    }
}
