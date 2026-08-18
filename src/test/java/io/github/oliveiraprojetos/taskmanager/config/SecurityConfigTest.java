package io.github.oliveiraprojetos.taskmanager.config;

import io.github.oliveiraprojetos.taskmanager.security.CustomAuthenticationEntryPoint;
import io.github.oliveiraprojetos.taskmanager.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Test
    void shouldConfigureBCryptPasswordEncoder() {

        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
    }

    @Test
    void shouldEncodeAndValidatePassword() {

        String password = "123456";

        String encodedPassword =
                passwordEncoder.encode(password);

        assertNotNull(encodedPassword);
        assertFalse(encodedPassword.isBlank());

        assertTrue(
                passwordEncoder.matches(
                        password,
                        encodedPassword
                )
        );

        assertFalse(
                passwordEncoder.matches(
                        "senha-incorreta",
                        encodedPassword
                )
        );
    }

    @Test
    void shouldConfigureJwtAuthenticationFilter() {

        assertNotNull(jwtAuthenticationFilter);
    }

    @Test
    void shouldConfigureAuthenticationEntryPoint() {

        assertNotNull(authenticationEntryPoint);
    }

    @Test
    void shouldAllowAccessToPublicEndpoint() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRequireAuthenticationForProtectedEndpoint() throws Exception {

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isUnauthorized());
    }
}