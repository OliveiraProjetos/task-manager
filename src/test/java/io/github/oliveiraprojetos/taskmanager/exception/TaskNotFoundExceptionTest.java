package io.github.oliveiraprojetos.taskmanager.exception;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskNotFoundExceptionTest {

    private static final UUID TASK_ID =
            UUID.fromString(
                    "11111111-1111-1111-1111-111111111111"
            );

    @Test
    void shouldCreateExceptionWithExpectedMessage() {

        TaskNotFoundException exception =
                new TaskNotFoundException(TASK_ID);

        assertEquals(
                "Task with ID " + TASK_ID + " not found.",
                exception.getMessage()
        );
    }
}