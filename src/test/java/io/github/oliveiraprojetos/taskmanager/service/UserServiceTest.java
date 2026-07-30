package io.github.oliveiraprojetos.taskmanager.service;

import io.github.oliveiraprojetos.taskmanager.dto.RegisterUserRequest;
import io.github.oliveiraprojetos.taskmanager.dto.UserResponse;
import io.github.oliveiraprojetos.taskmanager.exception.UsernameAlreadyExistsException;
import io.github.oliveiraprojetos.taskmanager.model.User;
import io.github.oliveiraprojetos.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static final String USERNAME = "teste";
    private static final String PASSWORD = "123456";
    private static final String ENCODED_PASSWORD = "senhaCriptografada";

    private RegisterUserRequest registerUserRequest() {
        return new RegisterUserRequest(USERNAME, PASSWORD);
    }

    private User existingUser() {
        return new User(USERNAME, ENCODED_PASSWORD);
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterUserRequest request = registerUserRequest();

        when(userRepository.findByUsername(request.username()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(request.password()))
                .thenReturn(ENCODED_PASSWORD);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.register(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        InOrder inOrder = inOrder(userRepository, passwordEncoder);

        inOrder.verify(userRepository)
                .findByUsername(request.username());

        inOrder.verify(passwordEncoder)
                .encode(request.password());

        inOrder.verify(userRepository)
                .save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(USERNAME, savedUser.getUsername());
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
        assertNotEquals(PASSWORD, savedUser.getPassword());

        assertEquals(USERNAME, response.username());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {

        RegisterUserRequest request = registerUserRequest();
        User user = existingUser();

        when(userRepository.findByUsername(request.username()))
                .thenReturn(Optional.of(user));

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> userService.register(request)
        );

        verify(userRepository).findByUsername(request.username());
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }
}