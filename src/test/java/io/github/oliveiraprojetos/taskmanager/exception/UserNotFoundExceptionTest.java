package io.github.oliveiraprojetos.taskmanager.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserNotFoundExceptionTest {

    private static final String USERNAME = "teste";

    @Test
    void shouldCreateExceptionWithExpectedMessage() {

        UserNotFoundException exception =
                new UserNotFoundException(USERNAME);

        assertEquals(
                "User not found: " + USERNAME,
                exception.getMessage()
        );
    }
}