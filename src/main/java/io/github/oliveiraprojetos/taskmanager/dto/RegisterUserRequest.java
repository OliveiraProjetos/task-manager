package io.github.oliveiraprojetos.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(

        @NotBlank(message = "Username is required.")
        @Size(max = 50, message = "Username must have at most 50 characters.")
        String username,

        @NotBlank(message = "Password is required.")
        @Size(min = 6, message = "Password must have at least 6 characters.")
        String password

) {
}