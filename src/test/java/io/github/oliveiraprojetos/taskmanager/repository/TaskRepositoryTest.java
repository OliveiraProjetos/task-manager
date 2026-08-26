package io.github.oliveiraprojetos.taskmanager.repository;

import io.github.oliveiraprojetos.taskmanager.enums.TaskPriority;
import io.github.oliveiraprojetos.taskmanager.enums.TaskStatus;
import io.github.oliveiraprojetos.taskmanager.model.Task;
import io.github.oliveiraprojetos.taskmanager.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    private static final TaskStatus STATUS =
            TaskStatus.PENDING;

    private static final TaskPriority PRIORITY =
            TaskPriority.MEDIUM;

    private static final LocalDate DUE_DATE =
            LocalDate.of(2026, 12, 30);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindTasksWhenUserHasTasks() {

        User user = userRepository.save(
                new User("teste", "senha")
        );

        Task task1 = new Task(
                "Task 1",
                "Descrição 1",
                STATUS,
                PRIORITY,
                DUE_DATE,
                user
        );

        Task task2 = new Task(
                "Task 2",
                "Descrição 2",
                STATUS,
                PRIORITY,
                DUE_DATE,
                user
        );

        taskRepository.save(task1);
        taskRepository.save(task2);

        List<Task> result =
                taskRepository.findAllByUserId(user.getId());

        assertEquals(2, result.size());

        assertTrue(
                result.stream()
                        .anyMatch(task ->
                                task.getTitle().equals("Task 1"))
        );

        assertTrue(
                result.stream()
                        .anyMatch(task ->
                                task.getTitle().equals("Task 2"))
        );
    }

    @Test
    void shouldFindTaskWhenBelongsToUser() {

        User user = userRepository.save(
                new User("teste", "senha")
        );

        Task task = taskRepository.save(
                new Task(
                        "Estudar Java",
                        "Estudar testes",
                        STATUS,
                        PRIORITY,
                        DUE_DATE,
                        user
                )
        );

        Optional<Task> result =
                taskRepository.findByIdAndUserId(
                        task.getId(),
                        user.getId()
                );

        assertTrue(result.isPresent());
        assertEquals(
                "Estudar Java",
                result.get().getTitle()
        );
    }

    @Test
    void shouldReturnEmptyWhenTaskBelongsToAnotherUser() {

        User user1 = userRepository.save(
                new User("usuario1", "senha")
        );

        User user2 = userRepository.save(
                new User("usuario2", "senha")
        );

        Task task = taskRepository.save(
                new Task(
                        "Task privada",
                        "Descrição",
                        STATUS,
                        PRIORITY,
                        DUE_DATE,
                        user1
                )
        );

        Optional<Task> result =
                taskRepository.findByIdAndUserId(
                        task.getId(),
                        user2.getId()
                );

        assertTrue(result.isEmpty());
    }
}