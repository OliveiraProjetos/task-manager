package io.github.oliveiraprojetos.taskmanager.security;

import io.github.oliveiraprojetos.taskmanager.model.User;
import io.github.oliveiraprojetos.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private static final String USERNAME = "teste";
    private static final String PASSWORD = "senhaCriptografada";

    @Test
    void shouldReturnUserWhenUsernameExists() {

        User user = new User(
                USERNAME,
                PASSWORD
        );

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        var result =
                userDetailsService.loadUserByUsername(USERNAME);

        assertEquals(user, result);
        assertEquals(USERNAME, result.getUsername());
        assertEquals(PASSWORD, result.getPassword());

        verify(userRepository)
                .findByUsername(USERNAME);
    }

    @Test
    void shouldThrowExceptionWhenUsernameDoesNotExist() {

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.empty());

        assertThrows(
                org.springframework.security.core.userdetails
                        .UsernameNotFoundException.class,
                () -> userDetailsService
                        .loadUserByUsername(USERNAME)
        );

        verify(userRepository)
                .findByUsername(USERNAME);
    }
}