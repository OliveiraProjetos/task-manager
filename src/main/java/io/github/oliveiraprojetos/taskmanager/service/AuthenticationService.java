package io.github.oliveiraprojetos.taskmanager.service;

import io.github.oliveiraprojetos.taskmanager.dto.LoginRequest;
import io.github.oliveiraprojetos.taskmanager.dto.LoginResponse;
import io.github.oliveiraprojetos.taskmanager.exception.InvalidCredentialsException;
import io.github.oliveiraprojetos.taskmanager.model.User;
import io.github.oliveiraprojetos.taskmanager.repository.UserRepository;
import io.github.oliveiraprojetos.taskmanager.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword())) {

            throw new InvalidCredentialsException();
        }

        return new LoginResponse(
                jwtService.generateToken(user)
        );
    }
}