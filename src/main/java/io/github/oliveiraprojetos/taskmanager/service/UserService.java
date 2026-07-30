package io.github.oliveiraprojetos.taskmanager.service;

import io.github.oliveiraprojetos.taskmanager.dto.RegisterUserRequest;
import io.github.oliveiraprojetos.taskmanager.dto.UserResponse;
import io.github.oliveiraprojetos.taskmanager.exception.UsernameAlreadyExistsException;
import io.github.oliveiraprojetos.taskmanager.model.User;
import io.github.oliveiraprojetos.taskmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterUserRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameAlreadyExistsException();
        }

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password())
        );

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername()
        );
    }
}