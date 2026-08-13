package io.github.oliveiraprojetos.taskmanager.repository;

import io.github.oliveiraprojetos.taskmanager.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserWhenUsernameExists() {

        User user = new User(
                "teste",
                "senhaCriptografada"
        );

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByUsername("teste");

        assertTrue(result.isPresent());
        assertEquals("teste", result.get().getUsername());
        assertEquals(
                "senhaCriptografada",
                result.get().getPassword()
        );
    }

    @Test
    void shouldReturnEmptyWhenUsernameDoesNotExist() {

        Optional<User> result =
                userRepository.findByUsername("naoExiste");

        assertTrue(result.isEmpty());
    }
}