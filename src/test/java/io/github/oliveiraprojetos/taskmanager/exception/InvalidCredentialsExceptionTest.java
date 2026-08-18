package io.github.oliveiraprojetos.taskmanager.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidCredentialsExceptionTest {

    private static final String EXPECTED_MESSAGE =
            "Invalid credentials.";

    @Test
    void shouldCreateExceptionWithExpectedMessage() {

        InvalidCredentialsException exception =
                new InvalidCredentialsException();

        assertEquals(
                EXPECTED_MESSAGE,
                exception.getMessage()
        );
    }
}