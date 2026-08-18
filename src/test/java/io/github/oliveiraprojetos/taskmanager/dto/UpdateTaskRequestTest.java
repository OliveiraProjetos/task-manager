package io.github.oliveiraprojetos.taskmanager.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UpdateTaskRequestTest {

    private static final String TITLE = "Título atualizado";
    private static final String DESCRIPTION = "Descrição atualizada.";

    private UpdateTaskRequest updateTaskRequest() {
        return new UpdateTaskRequest(
                TITLE,
                DESCRIPTION
        );
    }

    @Test
    void shouldCreateRequestWhenTitleAndDescriptionAreProvided() {

        UpdateTaskRequest request = updateTaskRequest();

        assertEquals(TITLE, request.title());
        assertEquals(DESCRIPTION, request.description());
    }

    @Test
    void shouldCreateRequestWhenTitleIsNull() {

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        null,
                        DESCRIPTION
                );

        assertNull(request.title());
        assertEquals(
                DESCRIPTION,
                request.description()
        );
    }

    @Test
    void shouldCreateRequestWhenDescriptionIsNull() {

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        TITLE,
                        null
                );

        assertEquals(
                TITLE,
                request.title()
        );

        assertNull(request.description());
    }

    @Test
    void shouldCreateRequestWhenTitleAndDescriptionAreNull() {

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        null,
                        null
                );

        assertNull(request.title());
        assertNull(request.description());
    }
}