package io.github.oliveiraprojetos.taskmanager.controller;

import io.github.oliveiraprojetos.taskmanager.dto.LoginRequest;
import io.github.oliveiraprojetos.taskmanager.dto.LoginResponse;
import io.github.oliveiraprojetos.taskmanager.dto.RegisterUserRequest;
import io.github.oliveiraprojetos.taskmanager.dto.UserResponse;
import io.github.oliveiraprojetos.taskmanager.model.User;
import io.github.oliveiraprojetos.taskmanager.service.AuthenticationService;
import io.github.oliveiraprojetos.taskmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthController(
            UserService userService,
            AuthenticationService authenticationService
    ) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authenticationService.login(request);
    }
}