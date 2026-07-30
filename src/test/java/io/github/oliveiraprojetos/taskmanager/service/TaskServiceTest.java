package io.github.oliveiraprojetos.taskmanager.service;

import io.github.oliveiraprojetos.taskmanager.dto.CreateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.dto.UpdateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.enums.TaskPriority;
import io.github.oliveiraprojetos.taskmanager.enums.TaskStatus;
import io.github.oliveiraprojetos.taskmanager.exception.TaskNotFoundException;
import io.github.oliveiraprojetos.taskmanager.model.Task;
import io.github.oliveiraprojetos.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private static final UUID TASK_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final String TITLE = "Estudar Mockito";
    private static final String DESCRIPTION = "Aprender verify()";

    private static final String EXISTING_TITLE = "Título antigo";
    private static final String EXISTING_DESCRIPTION = "Descrição antiga";

    private static final String UPDATED_TITLE = "Título novo";
    private static final String UPDATED_DESCRIPTION = "Descrição nova";

    private CreateTaskRequest createTaskRequest() {
        return new CreateTaskRequest(TITLE, DESCRIPTION);
    }

    private UpdateTaskRequest updateTaskRequest() {
        return new UpdateTaskRequest(
                UPDATED_TITLE,
                UPDATED_DESCRIPTION
        );
    }

    private Task createTask() {
        return new Task(TITLE, DESCRIPTION);
    }

    private Task existingTask() {
        return new Task(EXISTING_TITLE, EXISTING_DESCRIPTION);
    }

    @Test
    void shouldReturnTaskWhenTaskExists() {

        Task task = createTask();

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(TASK_ID);

        assertEquals(task, result);

        verify(taskRepository).findById(TASK_ID);
    }

    @Test
    void shouldThrowExceptionWhenTaskDoesNotExist() {

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTaskById(TASK_ID)
        );

        verify(taskRepository).findById(TASK_ID);
    }

    @Test
    void shouldCreateTaskWhenRequestIsValid() {

        CreateTaskRequest request = createTaskRequest();

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.createTask(request);

        assertEquals(TITLE, result.getTitle());
        assertEquals(DESCRIPTION, result.getDescription());

        assertEquals(TaskStatus.PENDING, result.getStatus());
        assertEquals(TaskPriority.MEDIUM, result.getPriority());

        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldReturnAllTasksWhenTasksExist() {

        Task task1 = new Task("Task 1", "Descrição 1");
        Task task2 = new Task("Task 2", "Descrição 2");

        List<Task> tasks = List.of(task1, task2);

        when(taskRepository.findAll())
                .thenReturn(tasks);

        List<Task> result = taskService.getTasks();

        assertEquals(tasks, result);

        verify(taskRepository).findAll();
    }

    @Test
    void shouldUpdateTaskWhenTaskExists() {

        Task task = existingTask();
        UpdateTaskRequest request = updateTaskRequest();

        LocalDateTime oldUpdatedAt = task.getUpdatedAt();

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(task))
                .thenReturn(task);

        Task result = taskService.updateTask(TASK_ID, request);

        verify(taskRepository).findById(TASK_ID);
        verify(taskRepository).save(task);

        assertEquals(UPDATED_TITLE, task.getTitle());
        assertEquals(UPDATED_DESCRIPTION, task.getDescription());

        assertTrue(task.getUpdatedAt().isAfter(oldUpdatedAt));

        assertSame(task, result);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTask() {

        UpdateTaskRequest request = updateTaskRequest();

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTask(TASK_ID, request)
        );

        verify(taskRepository).findById(TASK_ID);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldDeleteTaskWhenTaskExists() {

        Task task = createTask();

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.of(task));

        taskService.deleteTask(TASK_ID);

        verify(taskRepository).findById(TASK_ID);
        verify(taskRepository).delete(task);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTask() {

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.deleteTask(TASK_ID)
        );

        verify(taskRepository).findById(TASK_ID);
        verify(taskRepository, never()).delete(any(Task.class));
    }

}