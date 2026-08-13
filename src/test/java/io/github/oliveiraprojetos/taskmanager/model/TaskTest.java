package io.github.oliveiraprojetos.taskmanager.model;

import io.github.oliveiraprojetos.taskmanager.enums.TaskPriority;
import io.github.oliveiraprojetos.taskmanager.enums.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    private static final String USERNAME = "teste";
    private static final String PASSWORD = "123456";

    private static final String TITLE = "Estudar Java";
    private static final String DESCRIPTION = "Estudar testes unitários";

    private static final String UPDATED_TITLE = "Título atualizado";
    private static final String UPDATED_DESCRIPTION =
            "Descrição atualizada";

    private User user() {
        return new User(USERNAME, PASSWORD);
    }

    @Test
    void shouldCreateTaskWithDefaultValuesWhenTitleDescriptionAndUserAreProvided() {

        User user = user();

        Task task = new Task(
                TITLE,
                DESCRIPTION,
                user
        );

        assertNotNull(task.getId());

        assertEquals(TITLE, task.getTitle());
        assertEquals(DESCRIPTION, task.getDescription());
        assertEquals(user, task.getUser());

        assertEquals(
                TaskStatus.PENDING,
                task.getStatus()
        );

        assertEquals(
                TaskPriority.MEDIUM,
                task.getPriority()
        );

        assertNotNull(task.getCreatedAt());
        assertNotNull(task.getUpdatedAt());
    }

    @Test
    void shouldInitializeCreatedAtAndUpdatedAtWhenTaskIsCreated() {

        LocalDateTime before = LocalDateTime.now();

        Task task = new Task(
                TITLE,
                DESCRIPTION,
                user()
        );

        LocalDateTime after = LocalDateTime.now();

        assertTrue(
                !task.getCreatedAt().isBefore(before)
                        && !task.getCreatedAt().isAfter(after)
        );

        assertTrue(
                !task.getUpdatedAt().isBefore(before)
                        && !task.getUpdatedAt().isAfter(after)
        );
    }

    @Test
    void shouldUpdateTitleAndDescriptionWhenTaskIsUpdated() {

        Task task = new Task(
                TITLE,
                DESCRIPTION,
                user()
        );

        task.update(
                UPDATED_TITLE,
                UPDATED_DESCRIPTION
        );

        assertEquals(
                UPDATED_TITLE,
                task.getTitle()
        );

        assertEquals(
                UPDATED_DESCRIPTION,
                task.getDescription()
        );
    }

    @Test
    void shouldUpdateUpdatedAtWhenTaskIsUpdated() throws InterruptedException {

        Task task = new Task(
                TITLE,
                DESCRIPTION,
                user()
        );

        LocalDateTime oldUpdatedAt = task.getUpdatedAt();

        Thread.sleep(1);

        task.update(
                UPDATED_TITLE,
                UPDATED_DESCRIPTION
        );

        assertTrue(
                task.getUpdatedAt().isAfter(oldUpdatedAt)
        );
    }
}