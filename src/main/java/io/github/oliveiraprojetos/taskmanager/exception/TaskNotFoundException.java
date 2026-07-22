package io.github.oliveiraprojetos.taskmanager.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException() {
        super("Task not found.");
    }

}