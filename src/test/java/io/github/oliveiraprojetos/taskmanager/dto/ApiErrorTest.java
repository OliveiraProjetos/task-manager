package io.github.oliveiraprojetos.taskmanager.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApiErrorTest {

    private static final LocalDateTime TIMESTAMP =
            LocalDateTime.of(2026, 8, 14, 18, 30);

    private static final int STATUS = 401;
    private static final String ERROR = "Unauthorized";
    private static final String MESSAGE = "Authentication is required.";
    private static final String PATH = "/tasks";

    private ApiError apiError() {
        return new ApiError(
                TIMESTAMP,
                STATUS,
                ERROR,
                MESSAGE,
                PATH
        );
    }

    @Test
    void shouldCreateErrorWhenAllFieldsAreProvided() {

        ApiError error = apiError();

        assertEquals(TIMESTAMP, error.timestamp());
        assertEquals(STATUS, error.status());
        assertEquals(ERROR, error.error());
        assertEquals(MESSAGE, error.message());
        assertEquals(PATH, error.path());
    }

    @Test
    void shouldCreateErrorWhenTimestampIsNull() {

        ApiError error = new ApiError(
                null,
                STATUS,
                ERROR,
                MESSAGE,
                PATH
        );

        assertNull(error.timestamp());
        assertEquals(STATUS, error.status());
        assertEquals(ERROR, error.error());
        assertEquals(MESSAGE, error.message());
        assertEquals(PATH, error.path());
    }

    @Test
    void shouldCreateErrorWhenMessageIsNull() {

        ApiError error = new ApiError(
                TIMESTAMP,
                STATUS,
                ERROR,
                null,
                PATH
        );

        assertEquals(TIMESTAMP, error.timestamp());
        assertEquals(STATUS, error.status());
        assertEquals(ERROR, error.error());
        assertNull(error.message());
        assertEquals(PATH, error.path());
    }

    @Test
    void shouldCreateErrorWhenPathIsNull() {

        ApiError error = new ApiError(
                TIMESTAMP,
                STATUS,
                ERROR,
                MESSAGE,
                null
        );

        assertEquals(TIMESTAMP, error.timestamp());
        assertEquals(STATUS, error.status());
        assertEquals(ERROR, error.error());
        assertEquals(MESSAGE, error.message());
        assertNull(error.path());
    }
}