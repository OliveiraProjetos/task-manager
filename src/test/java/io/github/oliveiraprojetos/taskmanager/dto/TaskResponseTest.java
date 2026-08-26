package io.github.oliveiraprojetos.taskmanager.dto;

import io.github.oliveiraprojetos.taskmanager.enums.TaskPriority;
import io.github.oliveiraprojetos.taskmanager.enums.TaskStatus;
import io.github.oliveiraprojetos.taskmanager.model.Task;
import io.github.oliveiraprojetos.taskmanager.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaskResponseTest {

    private static final UUID TASK_ID =
            UUID.fromString(
                    "11111111-1111-1111-1111-111111111111"
            );

    private static final String USERNAME = "teste";
    private static final String PASSWORD = "senhaCriptografada";

    private static final String TITLE = "Estudar Mockito";
    private static final String DESCRIPTION = "Aprender testes unitários.";

    private static final TaskStatus STATUS =
            TaskStatus.PENDING;

    private static final TaskPriority PRIORITY =
            TaskPriority.MEDIUM;

    private static final LocalDate DUE_DATE =
            LocalDate.of(2026, 12, 30);

    private static final LocalDateTime CREATED_AT =
            LocalDateTime.of(
                    2026,
                    8,
                    10,
                    10,
                    30
            );

    private static final LocalDateTime UPDATED_AT =
            LocalDateTime.of(
                    2026,
                    8,
                    12,
                    15,
                    45
            );

    private Task task() {

        User user = new User(
                USERNAME,
                PASSWORD
        );

        Task task = new Task(
                TITLE,
                DESCRIPTION,
                STATUS,
                PRIORITY,
                DUE_DATE,
                user
        );

        ReflectionTestUtils.setField(
                task,
                "id",
                TASK_ID
        );

        ReflectionTestUtils.setField(
                task,
                "dueDate",
                DUE_DATE
        );

        ReflectionTestUtils.setField(
                task,
                "createdAt",
                CREATED_AT
        );

        ReflectionTestUtils.setField(
                task,
                "updatedAt",
                UPDATED_AT
        );

        return task;
    }

    @Test
    void shouldCreateResponseWhenTaskIsProvided() {

        TaskResponse response =
                TaskResponse.from(task());

        assertEquals(
                TASK_ID,
                response.id()
        );

        assertEquals(
                TITLE,
                response.title()
        );

        assertEquals(
                DESCRIPTION,
                response.description()
        );

        assertEquals(
                TaskStatus.PENDING,
                response.status()
        );

        assertEquals(
                TaskPriority.MEDIUM,
                response.priority()
        );

        assertEquals(
                DUE_DATE,
                response.dueDate()
        );

        assertEquals(
                CREATED_AT,
                response.createdAt()
        );

        assertEquals(
                UPDATED_AT,
                response.updatedAt()
        );
    }

    @Test
    void shouldCreateResponseWithNullDueDate() {

        Task task = task();

        ReflectionTestUtils.setField(
                task,
                "dueDate",
                null
        );

        TaskResponse response =
                TaskResponse.from(task);

        assertNull(response.dueDate());

        assertEquals(
                TITLE,
                response.title()
        );

        assertEquals(
                DESCRIPTION,
                response.description()
        );
    }
}