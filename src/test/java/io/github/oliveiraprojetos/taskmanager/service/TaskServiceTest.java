package io.github.oliveiraprojetos.taskmanager.service;

import io.github.oliveiraprojetos.taskmanager.dto.CreateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.dto.TaskResponse;
import io.github.oliveiraprojetos.taskmanager.dto.UpdateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.enums.TaskPriority;
import io.github.oliveiraprojetos.taskmanager.enums.TaskStatus;
import io.github.oliveiraprojetos.taskmanager.exception.TaskNotFoundException;
import io.github.oliveiraprojetos.taskmanager.exception.UserNotFoundException;
import io.github.oliveiraprojetos.taskmanager.model.Task;
import io.github.oliveiraprojetos.taskmanager.model.User;
import io.github.oliveiraprojetos.taskmanager.repository.TaskRepository;
import io.github.oliveiraprojetos.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private static final String USERNAME = "teste";
    private static final UUID USER_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    private static final UUID TASK_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final String TITLE = "Estudar Mockito";
    private static final String DESCRIPTION = "Aprender verify()";

    private static final String EXISTING_TITLE = "Título antigo";
    private static final String EXISTING_DESCRIPTION = "Descrição antiga";

    private static final String UPDATED_TITLE = "Título novo";
    private static final String UPDATED_DESCRIPTION = "Descrição nova";

    private User user() {
        return mock(User.class);
    }

    private CreateTaskRequest createTaskRequest() {
        return new CreateTaskRequest(TITLE, DESCRIPTION);
    }

    private UpdateTaskRequest updateTaskRequest() {
        return new UpdateTaskRequest(
                UPDATED_TITLE,
                UPDATED_DESCRIPTION
        );
    }

    @Test
    void shouldReturnTaskWhenTaskExists() {

        User user = user();

        when(user.getId()).thenReturn(USER_ID);

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        Task task = new Task(
                TITLE,
                DESCRIPTION,
                user
        );

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID
        )).thenReturn(Optional.of(task));

        TaskResponse result = taskService.getTaskById(
                TASK_ID,
                USERNAME
        );

        assertEquals(TITLE, result.title());
        assertEquals(DESCRIPTION, result.description());

        verify(userRepository)
                .findByUsername(USERNAME);

        verify(taskRepository)
                .findByIdAndUserId(TASK_ID, USER_ID);
    }

    @Test
    void shouldThrowExceptionWhenTaskDoesNotExist() {

        User user = user();

        when(user.getId()).thenReturn(USER_ID);

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID
        )).thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTaskById(
                        TASK_ID,
                        USERNAME
                )
        );

        verify(userRepository)
                .findByUsername(USERNAME);

        verify(taskRepository)
                .findByIdAndUserId(TASK_ID, USER_ID);
    }

    @Test
    void shouldCreateTaskWhenRequestIsValid() {

        User user = user();

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        CreateTaskRequest request = createTaskRequest();

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse result = taskService.createTask(
                request,
                USERNAME
        );

        assertEquals(TITLE, result.title());
        assertEquals(DESCRIPTION, result.description());

        assertEquals(TaskStatus.PENDING, result.status());
        assertEquals(TaskPriority.MEDIUM, result.priority());

        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());

        verify(userRepository)
                .findByUsername(USERNAME);

        ArgumentCaptor<Task> taskCaptor =
                ArgumentCaptor.forClass(Task.class);

        verify(taskRepository).save(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();

        assertEquals(TITLE, savedTask.getTitle());
        assertEquals(DESCRIPTION, savedTask.getDescription());
        assertEquals(user, savedTask.getUser());
    }

    @Test
    void shouldReturnTasksWhenAuthenticatedUserHasTasks() {

        User user = user();

        when(user.getId()).thenReturn(USER_ID);

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        Task task1 = new Task(
                "Task 1",
                "Descrição 1",
                user
        );

        Task task2 = new Task(
                "Task 2",
                "Descrição 2",
                user
        );

        List<Task> tasks = List.of(task1, task2);

        when(taskRepository.findAllByUserId(USER_ID))
                .thenReturn(tasks);

        List<TaskResponse> result =
                taskService.getTasks(USERNAME);

        assertEquals(2, result.size());

        assertEquals(
                "Task 1",
                result.get(0).title()
        );

        assertEquals(
                "Descrição 1",
                result.get(0).description()
        );

        assertEquals(
                "Task 2",
                result.get(1).title()
        );

        assertEquals(
                "Descrição 2",
                result.get(1).description()
        );

        verify(userRepository)
                .findByUsername(USERNAME);

        verify(taskRepository)
                .findAllByUserId(USER_ID);
    }

    @Test
    void shouldUpdateTaskWhenTaskBelongsToAuthenticatedUser() {

        User user = user();

        when(user.getId()).thenReturn(USER_ID);

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        Task task = new Task(
                EXISTING_TITLE,
                EXISTING_DESCRIPTION,
                user
        );

        LocalDateTime oldUpdatedAt = task.getUpdatedAt();

        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(task))
                .thenReturn(task);

        TaskResponse result = taskService.updateTask(
                TASK_ID,
                updateTaskRequest(),
                USERNAME
        );

        assertEquals(UPDATED_TITLE, result.title());
        assertEquals(UPDATED_DESCRIPTION, result.description());

        assertEquals(UPDATED_TITLE, task.getTitle());
        assertEquals(UPDATED_DESCRIPTION, task.getDescription());

        assertTrue(
                task.getUpdatedAt().isAfter(oldUpdatedAt)
        );

        verify(userRepository)
                .findByUsername(USERNAME);

        verify(taskRepository)
                .findByIdAndUserId(TASK_ID, USER_ID);

        verify(taskRepository)
                .save(task);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTask() {

        User user = user();

        when(user.getId()).thenReturn(USER_ID);

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTask(
                        TASK_ID,
                        updateTaskRequest(),
                        USERNAME
                )
        );

        verify(userRepository)
                .findByUsername(USERNAME);

        verify(taskRepository)
                .findByIdAndUserId(TASK_ID, USER_ID);

        verify(taskRepository, never())
                .save(any(Task.class));
    }

    @Test
    void shouldDeleteTaskWhenTaskBelongsToAuthenticatedUser() {

        User user = user();

        when(user.getId()).thenReturn(USER_ID);

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        Task task = new Task(
                TITLE,
                DESCRIPTION,
                user
        );

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID
        )).thenReturn(Optional.of(task));

        taskService.deleteTask(
                TASK_ID,
                USERNAME
        );

        verify(userRepository)
                .findByUsername(USERNAME);

        verify(taskRepository)
                .findByIdAndUserId(TASK_ID, USER_ID);

        verify(taskRepository)
                .delete(task);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTask() {

        User user = user();

        when(user.getId()).thenReturn(USER_ID);

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID
        )).thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.deleteTask(
                        TASK_ID,
                        USERNAME
                )
        );

        verify(userRepository)
                .findByUsername(USERNAME);

        verify(taskRepository)
                .findByIdAndUserId(TASK_ID, USER_ID);

        verify(taskRepository, never())
                .delete(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> taskService.getTasks(USERNAME)
        );

        verify(userRepository)
                .findByUsername(USERNAME);

        verifyNoInteractions(taskRepository);
    }
}