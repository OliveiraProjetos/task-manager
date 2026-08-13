package io.github.oliveiraprojetos.taskmanager.controller;

import io.github.oliveiraprojetos.taskmanager.dto.CreateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.dto.TaskResponse;
import io.github.oliveiraprojetos.taskmanager.dto.UpdateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getTasks(Authentication authentication) {

        return taskService.getTasks(
                authentication.getName()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication
    ) {

        return taskService.createTask(
                request,
                authentication.getName()
        );
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            @PathVariable UUID id,
            Authentication authentication
    ) {

        return taskService.getTaskById(
                id,
                authentication.getName()
        );
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable UUID id,
            @RequestBody UpdateTaskRequest request,
            Authentication authentication
    ) {

        return taskService.updateTask(
                id,
                request,
                authentication.getName()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable UUID id,
            Authentication authentication
    ) {

        taskService.deleteTask(
                id,
                authentication.getName()
        );
    }
}