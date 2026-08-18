package io.github.oliveiraprojetos.taskmanager.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsernameAlreadyExistsExceptionTest {

    private static final String USERNAME = "teste";

    @Test
    void shouldCreateExceptionWithExpectedMessage() {

        UsernameAlreadyExistsException exception =
                new UsernameAlreadyExistsException(USERNAME);

        assertEquals(
                "Username '" + USERNAME + "' already exists.",
                exception.getMessage()
        );
    }
}