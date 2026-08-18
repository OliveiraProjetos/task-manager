package io.github.oliveiraprojetos.taskmanager.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginRequestTest {

    private static final String VALID_USERNAME = "teste";
    private static final String VALID_PASSWORD = "123456";

    private static final Validator validator =
            ValidationTestUtils.validator();

    private LoginRequest loginRequest() {
        return new LoginRequest(
                VALID_USERNAME,
                VALID_PASSWORD
        );
    }

    private Set<ConstraintViolation<LoginRequest>> validate(
            LoginRequest request
    ) {
        return validator.validate(request);
    }

    @Test
    void shouldBeValidWhenUsernameAndPasswordAreValid() {

        LoginRequest request = loginRequest();

        Set<ConstraintViolation<LoginRequest>> violations =
                validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalidWhenUsernameIsNull() {

        LoginRequest request =
                new LoginRequest(
                        null,
                        VALID_PASSWORD
                );

        Set<ConstraintViolation<LoginRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<LoginRequest> violation =
                violations.iterator().next();

        assertEquals(
                "username",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Username is required.",
                violation.getMessage()
        );
    }

    @Test
    void shouldBeInvalidWhenUsernameIsBlank() {

        LoginRequest request =
                new LoginRequest(
                        "",
                        VALID_PASSWORD
                );

        Set<ConstraintViolation<LoginRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<LoginRequest> violation =
                violations.iterator().next();

        assertEquals(
                "username",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Username is required.",
                violation.getMessage()
        );
    }

    @Test
    void shouldBeInvalidWhenUsernameContainsOnlySpaces() {

        LoginRequest request =
                new LoginRequest(
                        "   ",
                        VALID_PASSWORD
                );

        Set<ConstraintViolation<LoginRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<LoginRequest> violation =
                violations.iterator().next();

        assertEquals(
                "username",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Username is required.",
                violation.getMessage()
        );
    }

    @Test
    void shouldBeInvalidWhenPasswordIsNull() {

        LoginRequest request =
                new LoginRequest(
                        VALID_USERNAME,
                        null
                );

        Set<ConstraintViolation<LoginRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<LoginRequest> violation =
                violations.iterator().next();

        assertEquals(
                "password",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Password is required.",
                violation.getMessage()
        );
    }

    @Test
    void shouldBeInvalidWhenPasswordIsBlank() {

        LoginRequest request =
                new LoginRequest(
                        VALID_USERNAME,
                        ""
                );

        Set<ConstraintViolation<LoginRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<LoginRequest> violation =
                violations.iterator().next();

        assertEquals(
                "password",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Password is required.",
                violation.getMessage()
        );
    }

    @Test
    void shouldBeInvalidWhenPasswordContainsOnlySpaces() {

        LoginRequest request =
                new LoginRequest(
                        VALID_USERNAME,
                        "   "
                );

        Set<ConstraintViolation<LoginRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<LoginRequest> violation =
                violations.iterator().next();

        assertEquals(
                "password",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Password is required.",
                violation.getMessage()
        );
    }
}