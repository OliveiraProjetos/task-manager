package io.github.oliveiraprojetos.taskmanager.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserResponseTest {

    private static final UUID USER_ID =
            UUID.fromString(
                    "22222222-2222-2222-2222-222222222222"
            );

    private static final String USERNAME = "teste";

    private UserResponse userResponse() {
        return new UserResponse(
                USER_ID,
                USERNAME
        );
    }

    @Test
    void shouldCreateResponseWhenIdAndUsernameAreProvided() {

        UserResponse response = userResponse();

        assertEquals(
                USER_ID,
                response.id()
        );

        assertEquals(
                USERNAME,
                response.username()
        );
    }

    @Test
    void shouldCreateResponseWhenIdIsNull() {

        UserResponse response =
                new UserResponse(
                        null,
                        USERNAME
                );

        assertNull(response.id());

        assertEquals(
                USERNAME,
                response.username()
        );
    }

    @Test
    void shouldCreateResponseWhenUsernameIsNull() {

        UserResponse response =
                new UserResponse(
                        USER_ID,
                        null
                );

        assertEquals(
                USER_ID,
                response.id()
        );

        assertNull(response.username());
    }

    @Test
    void shouldCreateResponseWhenIdAndUsernameAreNull() {

        UserResponse response =
                new UserResponse(
                        null,
                        null
                );

        assertNull(response.id());
        assertNull(response.username());
    }
}