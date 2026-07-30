package io.github.oliveiraprojetos.taskmanager.dto;

public record UpdateTaskRequest(

        String title,
        String description
) {
}