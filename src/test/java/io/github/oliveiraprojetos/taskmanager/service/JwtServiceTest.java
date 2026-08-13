package io.github.oliveiraprojetos.taskmanager.service;

import io.github.oliveiraprojetos.taskmanager.model.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String USERNAME = "teste";
    private static final String ENCODED_PASSWORD = "senhaCriptografada";

    private static final String SECRET =
            "uma-chave-super-secreta-com-pelo-menos-32-caracteres";

    private static final long EXPIRATION = 3_600_000L;

    private User existingUser() {
        return new User(USERNAME, ENCODED_PASSWORD);
    }

    private String generateValidToken() {
        return jwtService.generateToken(existingUser());
    }

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                SECRET
        );

        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                EXPIRATION
        );
    }

    @Test
    void shouldGenerateTokenWhenUserIsValid() {

        String token = generateValidToken();

        assertNotNull(token);
        assertFalse(token.isBlank());

        assertEquals(
                USERNAME,
                jwtService.extractUsername(token)
        );
    }

    @Test
    void shouldExtractUsernameWhenTokenIsValid() {

        String token = generateValidToken();

        String username = jwtService.extractUsername(token);

        assertEquals(USERNAME, username);
    }

    @Test
    void shouldReturnTrueWhenTokenIsValid() {

        String token = generateValidToken();

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void shouldReturnFalseWhenTokenIsInvalid() {

        assertFalse(jwtService.isTokenValid("token-invalido"));
    }

    @Test
    void shouldReturnFalseWhenTokenHasBeenTamperedWith() {

        String token = generateValidToken() + "abc";

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void shouldThrowJwtExceptionWhenTokenIsInvalid() {

        assertThrows(
                JwtException.class,
                () -> jwtService.extractUsername("token-invalido")
        );
    }
}