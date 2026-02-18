package se.bolagsverket.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import se.bolagsverket.security.dto.RegisterRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
class RegisterRequestValidationTest {

    private final Validator validator = TestValidators.validator();

    @Test
    void valid_registerRequest_passes_validation() {
        RegisterRequest request = validRegisterRequest();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void blank_username_fails_validation() {
        RegisterRequest request = validRegisterRequest();
        request.setUsername("");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "username", NotBlank.class);
    }

    @Test
    void null_username_fails_validation() {
        RegisterRequest request = validRegisterRequest();
        request.setUsername(null);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "username", NotBlank.class);
    }

    @Test
    void username_too_short_fails_validation() {
        RegisterRequest request = validRegisterRequest();
        request.setUsername("a");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "username", Size.class);
    }

    @Test
    void username_too_long_fails_validation() {
        RegisterRequest request = validRegisterRequest();
        request.setUsername("a".repeat(41));

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "username", Size.class);
    }

    @Test
    void blank_password_fails_validation() {
        RegisterRequest request = validRegisterRequest();
        request.setPassword("");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "password", NotBlank.class);
    }

    @Test
    void null_password_fails_validation() {
        RegisterRequest request = validRegisterRequest();
        request.setPassword(null);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "password", NotBlank.class);
    }

    @Test
    void password_too_short_fails_validation() {
        RegisterRequest request = validRegisterRequest();
        request.setPassword("short");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "password", Size.class);
    }

    @Test
    void password_too_long_fails_validation() {
        RegisterRequest request = validRegisterRequest();
        request.setPassword("a".repeat(101));

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertHasViolation(violations, "password", Size.class);
    }

    private void assertHasViolation(
            Set<ConstraintViolation<RegisterRequest>> violations,
            String field,
            Class<?> annotationType
    ) {
        assertTrue(
                violations.stream().anyMatch(v ->
                        v.getPropertyPath().toString().equals(field) &&
                                v.getConstraintDescriptor()
                                        .getAnnotation()
                                        .annotationType()
                                        .equals(annotationType)
                ),
                "Expected violation on field '" + field +
                        "' with constraint @" + annotationType.getSimpleName()
        );
    }

    private RegisterRequest validRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        return request;
    }
}