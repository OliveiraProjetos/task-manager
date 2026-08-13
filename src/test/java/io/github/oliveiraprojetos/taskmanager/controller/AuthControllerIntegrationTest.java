package io.github.oliveiraprojetos.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.oliveiraprojetos.taskmanager.dto.LoginRequest;
import io.github.oliveiraprojetos.taskmanager.dto.LoginResponse;
import io.github.oliveiraprojetos.taskmanager.dto.RegisterUserRequest;
import io.github.oliveiraprojetos.taskmanager.model.User;
import io.github.oliveiraprojetos.taskmanager.repository.UserRepository;
import io.github.oliveiraprojetos.taskmanager.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    private static final String REGISTER_URL = "/auth/register";
    private static final String LOGIN_URL = "/auth/login";

    private static final String USERNAME = "teste";
    private static final String PASSWORD = "123456";
    private static final String OLD_PASSWORD = "senha-antiga";
    private static final String INVALID_PASSWORD = "senha-errada";
    private static final String NON_EXISTING_USERNAME = "nao-existe";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfullyWhenValidCredentialsAreProvided() throws Exception {

        RegisterUserRequest request = registerRequest(USERNAME, PASSWORD);

        mockMvc.perform(
                        post(REGISTER_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value(USERNAME));

        User savedUser = findUserByUsername(USERNAME);

        assertEquals(USERNAME, savedUser.getUsername());
        assertNotEquals(PASSWORD, savedUser.getPassword());

        assertTrue(
                passwordEncoder.matches(
                        PASSWORD,
                        savedUser.getPassword()
                )
        );
    }

    @Test
    void shouldRejectRegistrationWhenUsernameAlreadyExists() throws Exception {

        saveUser(USERNAME, OLD_PASSWORD);

        RegisterUserRequest request = registerRequest(USERNAME, PASSWORD);

        mockMvc.perform(
                        post(REGISTER_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Username '" + USERNAME + "' already exists."))
                .andExpect(jsonPath("$.path").value(REGISTER_URL));

        assertEquals(1, userRepository.count());

        User savedUser = findUserByUsername(USERNAME);

        assertTrue(
                passwordEncoder.matches(
                        OLD_PASSWORD,
                        savedUser.getPassword()
                )
        );
    }

    @Test
    void shouldRejectRegistrationWhenUsernameIsBlank() throws Exception {

        RegisterUserRequest request = registerRequest("", PASSWORD);

        mockMvc.perform(
                        post(REGISTER_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(REGISTER_URL));

        assertEquals(0, userRepository.count());
    }

    @Test
    void shouldRejectLoginWhenPasswordIsBlank() throws Exception {

        LoginRequest request = loginRequest(USERNAME, "");

        mockMvc.perform(
                        post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value(LOGIN_URL));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {

        saveUser(USERNAME, PASSWORD);

        LoginRequest request = loginRequest(USERNAME, PASSWORD);

        String response = mockMvc.perform(
                        post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoginResponse loginResponse =
                objectMapper.readValue(response, LoginResponse.class);

        assertEquals(
                USERNAME,
                jwtService.extractUsername(loginResponse.token())
        );
    }

    @Test
    void shouldRejectLoginWhenUsernameDoesNotExist() throws Exception {

        LoginRequest request =
                loginRequest(NON_EXISTING_USERNAME, PASSWORD);

        mockMvc.perform(
                        post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid credentials."))
                .andExpect(jsonPath("$.path").value(LOGIN_URL));
    }

    @Test
    void shouldRejectLoginWhenPasswordIsIncorrect() throws Exception {

        saveUser(USERNAME, PASSWORD);

        LoginRequest request =
                loginRequest(USERNAME, INVALID_PASSWORD);

        mockMvc.perform(
                        post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid credentials."))
                .andExpect(jsonPath("$.path").value(LOGIN_URL));
    }

    private RegisterUserRequest registerRequest(
            String username,
            String password
    ) {
        return new RegisterUserRequest(username, password);
    }

    private LoginRequest loginRequest(String username, String password) {
        return new LoginRequest(username, password);
    }

    private void saveUser(String username, String password) {
        User user = new User(
                username,
                passwordEncoder.encode(password)
        );

        userRepository.save(user);
    }

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow();
    }

    private String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
}