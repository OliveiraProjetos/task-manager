package io.github.oliveiraprojetos.taskmanager.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterUserRequestTest {

    private static final String VALID_USERNAME = "teste";
    private static final String VALID_PASSWORD = "123456";

    private static final Validator validator =
            ValidationTestUtils.validator();

    private RegisterUserRequest registerUserRequest() {
        return new RegisterUserRequest(
                VALID_USERNAME,
                VALID_PASSWORD
        );
    }

    private Set<ConstraintViolation<RegisterUserRequest>> validate(
            RegisterUserRequest request
    ) {
        return validator.validate(request);
    }

    @Test
    void shouldBeValidWhenUsernameAndPasswordAreValid() {

        RegisterUserRequest request =
                registerUserRequest();

        Set<ConstraintViolation<RegisterUserRequest>> violations =
                validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalidWhenUsernameIsNull() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        null,
                        VALID_PASSWORD
                );

        Set<ConstraintViolation<RegisterUserRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<RegisterUserRequest> violation =
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

        RegisterUserRequest request =
                new RegisterUserRequest(
                        "",
                        VALID_PASSWORD
                );

        Set<ConstraintViolation<RegisterUserRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<RegisterUserRequest> violation =
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

        RegisterUserRequest request =
                new RegisterUserRequest(
                        "   ",
                        VALID_PASSWORD
                );

        Set<ConstraintViolation<RegisterUserRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<RegisterUserRequest> violation =
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
    void shouldBeValidWhenUsernameHasMaximumAllowedLength() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        "a".repeat(50),
                        VALID_PASSWORD
                );

        Set<ConstraintViolation<RegisterUserRequest>> violations =
                validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalidWhenUsernameExceedsMaximumLength() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        "a".repeat(51),
                        VALID_PASSWORD
                );

        Set<ConstraintViolation<RegisterUserRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<RegisterUserRequest> violation =
                violations.iterator().next();

        assertEquals(
                "username",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Username must have at most 50 characters.",
                violation.getMessage()
        );
    }

    @Test
    void shouldBeValidWhenPasswordHasMinimumAllowedLength() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        VALID_USERNAME,
                        "a".repeat(6)
                );

        Set<ConstraintViolation<RegisterUserRequest>> violations =
                validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalidWhenPasswordIsNull() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        VALID_USERNAME,
                        null
                );

        Set<ConstraintViolation<RegisterUserRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<RegisterUserRequest> violation =
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

        RegisterUserRequest request =
                new RegisterUserRequest(
                        VALID_USERNAME,
                        ""
                );

        Set<ConstraintViolation<RegisterUserRequest>> violations =
                validate(request);

        assertEquals(2, violations.size());

        assertTrue(
                violations.stream()
                        .allMatch(violation ->
                                violation.getPropertyPath()
                                        .toString()
                                        .equals("password")
                        )
        );

        assertTrue(
                violations.stream()
                        .anyMatch(violation ->
                                violation.getMessage()
                                        .equals("Password is required.")
                        )
        );

        assertTrue(
                violations.stream()
                        .anyMatch(violation ->
                                violation.getMessage()
                                        .equals(
                                                "Password must have at least 6 characters."
                                        )
                        )
        );
    }

    @Test
    void shouldBeInvalidWhenPasswordContainsOnlySpaces() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        VALID_USERNAME,
                        "      "
                );

        Set<ConstraintViolation<RegisterUserRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<RegisterUserRequest> violation =
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
    void shouldBeInvalidWhenPasswordIsShorterThanMinimumLength() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        VALID_USERNAME,
                        "12345"
                );

        Set<ConstraintViolation<RegisterUserRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<RegisterUserRequest> violation =
                violations.iterator().next();

        assertEquals(
                "password",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Password must have at least 6 characters.",
                violation.getMessage()
        );
    }
}