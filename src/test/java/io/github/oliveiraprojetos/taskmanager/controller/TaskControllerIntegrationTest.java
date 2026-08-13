package io.github.oliveiraprojetos.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.oliveiraprojetos.taskmanager.dto.CreateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.dto.UpdateTaskRequest;
import io.github.oliveiraprojetos.taskmanager.model.Task;
import io.github.oliveiraprojetos.taskmanager.model.User;
import io.github.oliveiraprojetos.taskmanager.repository.TaskRepository;
import io.github.oliveiraprojetos.taskmanager.repository.UserRepository;
import io.github.oliveiraprojetos.taskmanager.service.JwtService;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    private static final String TASKS_URL = "/tasks";

    private static final String USERNAME = "teste";
    private static final String ANOTHER_USERNAME = "outro-usuario";
    private static final String PASSWORD = "123456";

    private static final String TASK_TITLE = "Estudar Spring Boot";
    private static final String TASK_DESCRIPTION =
            "Estudar testes de integração";

    private static final String UPDATED_TITLE = "Título atualizado";
    private static final String UPDATED_DESCRIPTION =
            "Descrição atualizada";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    class GetTasks {

        @Test
        void shouldGetTasksWhenAuthenticated() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            Task savedTask = saveTask(user);

            mockMvc.perform(
                            get(TASKS_URL)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id")
                            .value(savedTask.getId().toString()))
                    .andExpect(jsonPath("$[0].title")
                            .value(TASK_TITLE))
                    .andExpect(jsonPath("$[0].description")
                            .value(TASK_DESCRIPTION));
        }

        @Test
        void shouldGetEmptyListWhenAuthenticatedAndNoTasksExist() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            mockMvc.perform(
                            get(TASKS_URL)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void shouldGetOnlyAuthenticatedUserTasksWhenAuthenticated() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            Task userTask = saveTask(user);

            User anotherUser = saveAnotherUser();
            saveTask(anotherUser);

            mockMvc.perform(
                            get(TASKS_URL)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id")
                            .value(userTask.getId().toString()))
                    .andExpect(jsonPath("$[0].title")
                            .value(TASK_TITLE))
                    .andExpect(jsonPath("$[0].description")
                            .value(TASK_DESCRIPTION));
        }

        @Test
        void shouldRejectGetTasksWhenNotAuthenticated() throws Exception {

            mockMvc.perform(get(TASKS_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldRejectGetTasksWhenTokenIsInvalid() throws Exception {

            mockMvc.perform(
                            get(TASKS_URL)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken("token-invalido")
                                    )
                    )
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldRejectGetTasksWhenAuthorizationHeaderIsMalformed() throws Exception {

            mockMvc.perform(
                            get(TASKS_URL)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            "Bearer"
                                    )
                    )
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldRejectGetTasksWhenTokenIsExpired() throws Exception {

            User user = saveUser();

            SecretKey key = hmacShaKeyFor(
                    "1234567890123456789012345678901234567890123456789012345678901234"
                            .getBytes(StandardCharsets.UTF_8)
            );

            long now = System.currentTimeMillis();

            String expiredToken = Jwts.builder()
                    .subject(user.getUsername())
                    .issuedAt(new Date(now - 10_000))
                    .expiration(new Date(now - 5_000))
                    .signWith(key)
                    .compact();

            mockMvc.perform(
                            get(TASKS_URL)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(expiredToken)
                                    )
                    )
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldRejectGetTasksWhenTokenUserDoesNotExist() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            userRepository.delete(user);

            mockMvc.perform(
                            get(TASKS_URL)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                    )
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class CreateTask {

        @Test
        void shouldCreateTaskWhenAuthenticated() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            CreateTaskRequest request = new CreateTaskRequest(
                    TASK_TITLE,
                    TASK_DESCRIPTION
            );

            mockMvc.perform(
                            post(TASKS_URL)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(request))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.title")
                            .value(TASK_TITLE))
                    .andExpect(jsonPath("$.description")
                            .value(TASK_DESCRIPTION))
                    .andExpect(jsonPath("$.status")
                            .value("PENDING"))
                    .andExpect(jsonPath("$.priority")
                            .value("MEDIUM"));

            assertEquals(1, taskRepository.count());

            Task savedTask = taskRepository.findAll().getFirst();

            assertNotNull(savedTask.getId());
            assertEquals(TASK_TITLE, savedTask.getTitle());
            assertEquals(TASK_DESCRIPTION, savedTask.getDescription());

            assertEquals(
                    user.getId(),
                    savedTask.getUser().getId()
            );
        }

        @Test
        void shouldRejectCreateTaskWhenNotAuthenticated() throws Exception {

            CreateTaskRequest request = new CreateTaskRequest(
                    TASK_TITLE,
                    TASK_DESCRIPTION
            );

            mockMvc.perform(
                            post(TASKS_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(request))
                    )
                    .andExpect(status().isUnauthorized());

            assertEquals(0, taskRepository.count());
        }

        @Test
        void shouldRejectCreateTaskWhenTitleIsBlank() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            CreateTaskRequest request = new CreateTaskRequest(
                    "",
                    TASK_DESCRIPTION
            );

            mockMvc.perform(
                            post(TASKS_URL)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(request))
                    )
                    .andExpect(status().isBadRequest());

            assertEquals(0, taskRepository.count());
        }

        @Test
        void shouldRejectCreateTaskWhenTitleExceedsMaximumLength() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            String title = "a".repeat(101);

            CreateTaskRequest request = new CreateTaskRequest(
                    title,
                    TASK_DESCRIPTION
            );

            mockMvc.perform(
                            post(TASKS_URL)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(request))
                    )
                    .andExpect(status().isBadRequest());

            assertEquals(0, taskRepository.count());
        }

        @Test
        void shouldCreateTaskWhenDescriptionIsNull() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            CreateTaskRequest request = new CreateTaskRequest(
                    TASK_TITLE,
                    null
            );

            mockMvc.perform(
                            post(TASKS_URL)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(request))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title")
                            .value(TASK_TITLE))
                    .andExpect(jsonPath("$.description")
                            .doesNotExist());

            assertEquals(1, taskRepository.count());
        }
    }

    @Nested
    class GetTaskById {

        @Test
        void shouldGetTaskByIdWhenAuthenticated() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            Task savedTask = saveTask(user);

            mockMvc.perform(
                            get(taskUrl(savedTask.getId()))
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id")
                            .value(savedTask.getId().toString()))
                    .andExpect(jsonPath("$.title")
                            .value(TASK_TITLE))
                    .andExpect(jsonPath("$.description")
                            .value(TASK_DESCRIPTION));
        }

        @Test
        void shouldRejectGetTaskByIdWhenNotAuthenticated() throws Exception {

            User user = saveUser();

            Task savedTask = saveTask(user);

            mockMvc.perform(
                            get(taskUrl(savedTask.getId()))
                    )
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldReturnNotFoundWhenGettingNonExistingTask() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            UUID id = UUID.randomUUID();

            mockMvc.perform(
                            get(taskUrl(id))
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldNotGetTaskWhenTaskBelongsToAnotherUser() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            User anotherUser = saveAnotherUser();

            Task task = saveTask(anotherUser);

            mockMvc.perform(
                            get(taskUrl(task.getId()))
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                    )
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class UpdateTask {

        @Test
        void shouldUpdateTaskWhenAuthenticated() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            Task savedTask = saveTask(user);

            UpdateTaskRequest request = new UpdateTaskRequest(
                    UPDATED_TITLE,
                    UPDATED_DESCRIPTION
            );

            mockMvc.perform(
                            put(taskUrl(savedTask.getId()))
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(request))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id")
                            .value(savedTask.getId().toString()))
                    .andExpect(jsonPath("$.title")
                            .value(UPDATED_TITLE))
                    .andExpect(jsonPath("$.description")
                            .value(UPDATED_DESCRIPTION));

            Task updatedTask = taskRepository
                    .findById(savedTask.getId())
                    .orElseThrow();

            assertEquals(UPDATED_TITLE, updatedTask.getTitle());
            assertEquals(
                    UPDATED_DESCRIPTION,
                    updatedTask.getDescription()
            );
        }

        @Test
        void shouldRejectUpdateTaskWhenNotAuthenticated() throws Exception {

            User user = saveUser();

            Task savedTask = saveTask(user);

            UpdateTaskRequest request = new UpdateTaskRequest(
                    UPDATED_TITLE,
                    UPDATED_DESCRIPTION
            );

            mockMvc.perform(
                            put(taskUrl(savedTask.getId()))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(request))
                    )
                    .andExpect(status().isUnauthorized());

            Task unchangedTask = taskRepository
                    .findById(savedTask.getId())
                    .orElseThrow();

            assertEquals(
                    TASK_TITLE,
                    unchangedTask.getTitle()
            );

            assertEquals(
                    TASK_DESCRIPTION,
                    unchangedTask.getDescription()
            );
        }

        @Test
        void shouldReturnNotFoundWhenUpdatingNonExistingTask() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            UUID id = UUID.randomUUID();

            UpdateTaskRequest request = new UpdateTaskRequest(
                    UPDATED_TITLE,
                    UPDATED_DESCRIPTION
            );

            mockMvc.perform(
                            put(taskUrl(id))
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(request))
                    )
                    .andExpect(status().isNotFound());

            assertEquals(0, taskRepository.count());
        }

        @Test
        void shouldNotUpdateTaskWhenTaskBelongsToAnotherUser() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            User anotherUser = saveAnotherUser();

            Task task = saveTask(anotherUser);

            UpdateTaskRequest request = new UpdateTaskRequest(
                    "Título alterado",
                    "Descrição alterada"
            );

            mockMvc.perform(
                            put(taskUrl(task.getId()))
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(toJson(request))
                    )
                    .andExpect(status().isNotFound());

            Task unchangedTask = taskRepository
                    .findById(task.getId())
                    .orElseThrow();

            assertEquals(
                    TASK_TITLE,
                    unchangedTask.getTitle()
            );

            assertEquals(
                    TASK_DESCRIPTION,
                    unchangedTask.getDescription()
            );
        }
    }

    @Nested
    class DeleteTask {

        @Test
        void shouldDeleteTaskWhenAuthenticated() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            Task savedTask = saveTask(user);

            mockMvc.perform(
                            delete(taskUrl(savedTask.getId()))
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                    )
                    .andExpect(status().isNoContent());

            assertEquals(0, taskRepository.count());
        }

        @Test
        void shouldRejectDeleteTaskWhenNotAuthenticated() throws Exception {

            User user = saveUser();

            Task savedTask = saveTask(user);

            mockMvc.perform(
                            delete(taskUrl(savedTask.getId()))
                    )
                    .andExpect(status().isUnauthorized());

            assertEquals(1, taskRepository.count());
        }

        @Test
        void shouldReturnNotFoundWhenDeletingNonExistingTask() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            UUID id = UUID.randomUUID();

            mockMvc.perform(
                            delete(taskUrl(id))
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldNotDeleteTaskWhenTaskBelongsToAnotherUser() throws Exception {

            User user = saveUser();
            String token = generateToken(user);

            User anotherUser = saveAnotherUser();

            Task task = saveTask(anotherUser);

            mockMvc.perform(
                            delete(taskUrl(task.getId()))
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            bearerToken(token)
                                    )
                    )
                    .andExpect(status().isNotFound());

            assertEquals(1, taskRepository.count());

            assertNotNull(
                    taskRepository.findById(task.getId())
                            .orElse(null)
            );
        }
    }

    private User saveUser() {

        User user = new User(
                USERNAME,
                passwordEncoder.encode(PASSWORD)
        );

        return userRepository.save(user);
    }

    private User saveAnotherUser() {

        User user = new User(
                ANOTHER_USERNAME,
                passwordEncoder.encode(PASSWORD)
        );

        return userRepository.save(user);
    }

    private String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    private Task saveTask(User user) {

        Task task = new Task(
                TASK_TITLE,
                TASK_DESCRIPTION,
                user
        );

        return taskRepository.save(task);
    }

    private String bearerToken(String token) {
        return "Bearer " + token;
    }

    private String taskUrl(UUID id) {
        return TASKS_URL + "/" + id;
    }

    private String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
}