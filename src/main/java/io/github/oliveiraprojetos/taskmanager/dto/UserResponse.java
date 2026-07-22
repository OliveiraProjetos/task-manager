package io.github.oliveiraprojetos.taskmanager.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username
) {
}