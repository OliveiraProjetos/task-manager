package io.github.oliveiraprojetos.taskmanager.controller;

import io.github.oliveiraprojetos.taskmanager.dto.CreateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.dto.TaskResponse;
import io.github.oliveiraprojetos.taskmanager.dto.UpdateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@Tag(
        name = "Tasks",
        description = "Endpoints for task management."
)
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
            summary = "List the authenticated user's tasks",
            description = "Returns only the tasks belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task list returned successfully."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated."
            )
    })
    @GetMapping
    public List<TaskResponse> getTasks(Authentication authentication) {

        return taskService.getTasks(
                authentication.getName()
        );
    }

    @Operation(
            summary = "Create a new task",
            description = "Creates a task for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Task created successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated."
            )
    })
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

    @Operation(
            summary = "Find a task by ID",
            description = "Returns a task belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task retrieved successfully."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found."
            )
    })
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

    @Operation(
            summary = "Updates a task",
            description = "Updates a task only if it belongs to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task updated successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found."
            )
    })
    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request,
            Authentication authentication
    ) {
        return taskService.updateTask(
                id,
                request,
                authentication.getName()
        );
    }

    @Operation(
            summary = "Deletes a task",
            description = "Deletes a task only if it belongs to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Task deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found."
            )
    })
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