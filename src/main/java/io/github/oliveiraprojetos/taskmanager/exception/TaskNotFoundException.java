package io.github.oliveiraprojetos.taskmanager.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(UUID id) {
        super("Task with ID " + id + " not found.");
    }

}