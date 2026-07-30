package io.github.oliveiraprojetos.taskmanager.exception;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException() {
        super("Username already exists.");
    }
}
