package io.github.oliveiraprojetos.taskmanager.model;

import io.github.oliveiraprojetos.taskmanager.enums.TaskPriority;
import io.github.oliveiraprojetos.taskmanager.enums.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    private static final String USERNAME = "teste";
    private static final String PASSWORD = "123456";

    private static final String TITLE = "Estudar Java";
    private static final String DESCRIPTION = "Estudar testes unitários";

    private static final TaskStatus STATUS =
            TaskStatus.PENDING;

    private static final TaskPriority PRIORITY =
            TaskPriority.MEDIUM;

    private static final LocalDate DUE_DATE =
            LocalDate.of(2026, 12, 30);

    private static final String UPDATED_TITLE = "Título atualizado";
    private static final String UPDATED_DESCRIPTION = "Descrição atualizada";

    private static final TaskStatus UPDATED_STATUS =
            TaskStatus.COMPLETED;

    private static final TaskPriority UPDATED_PRIORITY =
            TaskPriority.HIGH;

    private static final LocalDate UPDATED_DUE_DATE =
            LocalDate.of(2026, 8, 20);

    private User user() {
        return new User(USERNAME, PASSWORD);
    }

    private Task task() {
        return new Task(
                TITLE,
                DESCRIPTION,
                STATUS,
                PRIORITY,
                DUE_DATE,
                user()
        );
    }

    @Test
    void shouldCreateTaskWithWhenValuesAreProvided() {

        User user = user();

        Task task = new Task(
                TITLE,
                DESCRIPTION,
                STATUS,
                PRIORITY,
                DUE_DATE,
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
    void shouldUseDefaultStatusWhenStatusIsNull() {

        Task task = new Task(
                TITLE,
                DESCRIPTION,
                null,
                PRIORITY,
                DUE_DATE,
                user()
        );

        assertEquals(
                TaskStatus.PENDING,
                task.getStatus()
        );
    }

    @Test
    void shouldUseDefaultPriorityWhenPriorityIsNull() {

        Task task = new Task(
                TITLE,
                DESCRIPTION,
                STATUS,
                null,
                DUE_DATE,
                user()
        );

        assertEquals(
                TaskPriority.MEDIUM,
                task.getPriority()
        );
    }

    @Test
    void shouldKeepDueDateNullWhenDueDateIsNull() {

        Task task = new Task(
                TITLE,
                DESCRIPTION,
                STATUS,
                PRIORITY,
                null,
                user()
        );

        assertNull(task.getDueDate());
    }

    @Test
    void shouldInitializeCreatedAtAndUpdatedAtWhenTaskIsCreated() {

        LocalDateTime before = LocalDateTime.now();

        Task task = task();

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

        Task task = task();

        task.update(
                UPDATED_TITLE,
                UPDATED_DESCRIPTION,
                TaskStatus.PENDING,
                TaskPriority.MEDIUM,
                null
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
    void shouldUpdateStatusPriorityAndDueDateWhenTaskIsUpdated() {

        Task task = task();

        task.update(
                TITLE,
                DESCRIPTION,
                UPDATED_STATUS,
                UPDATED_PRIORITY,
                UPDATED_DUE_DATE
        );

        assertEquals(
                UPDATED_STATUS,
                task.getStatus()
        );

        assertEquals(
                UPDATED_PRIORITY,
                task.getPriority()
        );

        assertEquals(
                UPDATED_DUE_DATE,
                task.getDueDate()
        );
    }

    @Test
    void shouldUpdateUpdatedAtWhenTaskIsUpdated() throws InterruptedException {

        Task task = task();

        LocalDateTime oldUpdatedAt = task.getUpdatedAt();

        Thread.sleep(1);

        task.update(
                TITLE,
                DESCRIPTION,
                UPDATED_STATUS,
                UPDATED_PRIORITY,
                UPDATED_DUE_DATE
        );

        assertTrue(
                task.getUpdatedAt().isAfter(oldUpdatedAt)
        );
    }
}