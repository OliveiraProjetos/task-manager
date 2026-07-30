package io.github.oliveiraprojetos.taskmanager.service;

import io.github.oliveiraprojetos.taskmanager.dto.LoginRequest;
import io.github.oliveiraprojetos.taskmanager.dto.LoginResponse;
import io.github.oliveiraprojetos.taskmanager.exception.InvalidCredentialsException;
import io.github.oliveiraprojetos.taskmanager.model.User;
import io.github.oliveiraprojetos.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private static final String USERNAME = "teste";
    private static final String PASSWORD = "123456";
    private static final String ENCODED_PASSWORD = "senhaCriptografada";
    private static final String TOKEN = "token123";

    private LoginRequest loginRequest() {
        return new LoginRequest(USERNAME, PASSWORD);
    }

    private User createUser() {
        return new User(USERNAME, ENCODED_PASSWORD);
    }

    @Test
    void shouldReturnJwtTokenWhenCredentialsAreValid() {

        LoginRequest request = loginRequest();
        User user = createUser();

        when(userRepository.findByUsername(request.username()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )).thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn(TOKEN);

        LoginResponse response = authenticationService.login(request);

        assertEquals(TOKEN, response.token());

        verify(userRepository).findByUsername(request.username());
        verify(passwordEncoder)
                .matches(request.password(), user.getPassword());
        verify(jwtService).generateToken(user);

    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        LoginRequest request = loginRequest();

        when(userRepository.findByUsername(request.username()))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticationService.login(request)
        );

        verify(userRepository).findByUsername(request.username());
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {

        LoginRequest request = loginRequest();
        User user = createUser();

        when(userRepository.findByUsername(request.username()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticationService.login(request)
        );

        verify(userRepository).findByUsername(request.username());
        verify(passwordEncoder)
                .matches(request.password(), user.getPassword());
        verifyNoInteractions(jwtService);
    }
}
