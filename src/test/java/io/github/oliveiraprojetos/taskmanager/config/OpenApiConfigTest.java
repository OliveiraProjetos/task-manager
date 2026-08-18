package io.github.oliveiraprojetos.taskmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenApiConfigTest {

    private final OpenApiConfig openApiConfig =
            new OpenApiConfig();

    @Test
    void shouldCreateOpenApiConfiguration() {

        OpenAPI openAPI =
                openApiConfig.taskManagerOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());

        assertEquals(
                "Task Manager API",
                openAPI.getInfo().getTitle()
        );

        assertEquals(
                "API para gerenciamento de tarefas desenvolvida com Spring Boot.",
                openAPI.getInfo().getDescription()
        );

        assertEquals(
                "1.0.0",
                openAPI.getInfo().getVersion()
        );
    }
}