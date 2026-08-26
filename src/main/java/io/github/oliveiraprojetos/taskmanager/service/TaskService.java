package io.github.oliveiraprojetos.taskmanager.service;

import io.github.oliveiraprojetos.taskmanager.dto.CreateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.dto.TaskResponse;
import io.github.oliveiraprojetos.taskmanager.dto.UpdateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.exception.TaskNotFoundException;
import io.github.oliveiraprojetos.taskmanager.exception.UserNotFoundException;
import io.github.oliveiraprojetos.taskmanager.model.Task;
import io.github.oliveiraprojetos.taskmanager.model.User;
import io.github.oliveiraprojetos.taskmanager.repository.TaskRepository;
import io.github.oliveiraprojetos.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<TaskResponse> getTasks(String username) {

        User user = findUser(username);

        return taskRepository
                .findAllByUserId(user.getId())
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    public TaskResponse createTask(
            CreateTaskRequest request,
            String username
    ) {

        User user = findUser(username);

        Task task = new Task(
                request.title(),
                request.description(),
                request.status(),
                request.priority(),
                request.dueDate(),
                user
        );

        Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    public TaskResponse getTaskById(
            UUID taskId,
            String username
    ) {

        User user = findUser(username);

        Task task = taskRepository
                .findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        return TaskResponse.from(task);
    }

    public TaskResponse updateTask(
            UUID taskId,
            UpdateTaskRequest request,
            String username
    ) {

        User user = findUser(username);

        Task task = taskRepository
                .findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        task.update(
                request.title(),
                request.description(),
                request.status(),
                request.priority(),
                request.dueDate()
        );

        Task updatedTask = taskRepository.save(task);

        return TaskResponse.from(updatedTask);
    }

    public void deleteTask(
            UUID taskId,
            String username
    ) {

        User user = findUser(username);

        Task task = taskRepository
                .findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        taskRepository.delete(task);
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}