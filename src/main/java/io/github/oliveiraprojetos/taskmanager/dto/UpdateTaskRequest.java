package io.github.oliveiraprojetos.taskmanager.dto;

import io.github.oliveiraprojetos.taskmanager.enums.TaskPriority;
import io.github.oliveiraprojetos.taskmanager.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateTaskRequest(

        @NotBlank(message = "Title is required.")
        @Size(max = 100, message = "Title must have at most 100 characters.")
        String title,

        @Size(max = 500, message = "Description must have at most 500 characters.")
        String description,

        TaskStatus status,

        TaskPriority priority,

        LocalDate dueDate
) {
}