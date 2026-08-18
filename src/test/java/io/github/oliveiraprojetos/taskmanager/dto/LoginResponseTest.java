package io.github.oliveiraprojetos.taskmanager.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoginResponseTest {

    private static final String TOKEN = "token123";

    private LoginResponse loginResponse() {
        return new LoginResponse(TOKEN);
    }

    @Test
    void shouldCreateResponseWhenTokenIsProvided() {

        LoginResponse response = loginResponse();

        assertEquals(
                TOKEN,
                response.token()
        );
    }

    @Test
    void shouldCreateResponseWhenTokenIsNull() {

        LoginResponse response =
                new LoginResponse(null);

        assertNull(response.token());
    }
}