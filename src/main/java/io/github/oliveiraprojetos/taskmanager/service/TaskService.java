package io.github.oliveiraprojetos.taskmanager.service;

import io.github.oliveiraprojetos.taskmanager.dto.CreateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.model.Task;
import io.github.oliveiraprojetos.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import io.github.oliveiraprojetos.taskmanager.dto.UpdateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.exception.TaskNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    public Task createTask(CreateTaskRequest request) {

        Task task = new Task(
                request.title(),
                request.description()
        );

        return taskRepository.save(task);
    }

    public Task getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(TaskNotFoundException::new);
    }

    public Task updateTask(UUID id, UpdateTaskRequest request) {

        Task task = taskRepository.findById(id)
                .orElseThrow(TaskNotFoundException::new);

        task.update(
                request.getTitle(),
                request.getDescription()
        );

        return taskRepository.save(task);
    }

    public void deleteTask(UUID id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(TaskNotFoundException::new);

        taskRepository.delete(task);

    }

}