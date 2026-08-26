package io.github.oliveiraprojetos.taskmanager.dto;

import io.github.oliveiraprojetos.taskmanager.enums.TaskPriority;
import io.github.oliveiraprojetos.taskmanager.enums.TaskStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateTaskRequestTest {

    private static final String VALID_TITLE = "Título atualizado";
    private static final String VALID_DESCRIPTION = "Descrição atualizada.";

    private static final TaskStatus VALID_STATUS =
            TaskStatus.IN_PROGRESS;

    private static final TaskPriority VALID_PRIORITY =
            TaskPriority.HIGH;

    private static final LocalDate VALID_DUE_DATE =
            LocalDate.of(2026, 12, 31);

    private static final Validator validator =
            ValidationTestUtils.validator();

    private UpdateTaskRequest updateTaskRequest() {
        return new UpdateTaskRequest(
                VALID_TITLE,
                VALID_DESCRIPTION,
                VALID_STATUS,
                VALID_PRIORITY,
                VALID_DUE_DATE
        );
    }

    private Set<ConstraintViolation<UpdateTaskRequest>> validate(
            UpdateTaskRequest request
    ) {
        return validator.validate(request);
    }

    @Test
    void shouldCreateRequestWhenAllFieldsAreProvided() {

        UpdateTaskRequest request = updateTaskRequest();

        assertEquals(
                VALID_TITLE,
                request.title()
        );

        assertEquals(
                VALID_DESCRIPTION,
                request.description()
        );

        assertEquals(
                VALID_STATUS,
                request.status()
        );

        assertEquals(
                VALID_PRIORITY,
                request.priority()
        );

        assertEquals(
                VALID_DUE_DATE,
                request.dueDate()
        );
    }

    @Test
    void shouldCreateRequestWhenDescriptionIsNull() {

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        VALID_TITLE,
                        null,
                        VALID_STATUS,
                        VALID_PRIORITY,
                        VALID_DUE_DATE
                );

        assertEquals(
                VALID_TITLE,
                request.title()
        );

        assertNull(request.description());

        assertEquals(
                VALID_STATUS,
                request.status()
        );

        assertEquals(
                VALID_PRIORITY,
                request.priority()
        );

        assertEquals(
                VALID_DUE_DATE,
                request.dueDate()
        );
    }

    @Test
    void shouldBeInvalidWhenTitleIsNull() {

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        null,
                        VALID_DESCRIPTION,
                        VALID_STATUS,
                        VALID_PRIORITY,
                        VALID_DUE_DATE
                );

        Set<ConstraintViolation<UpdateTaskRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<UpdateTaskRequest> violation =
                violations.iterator().next();

        assertEquals(
                "title",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Title is required.",
                violation.getMessage()
        );
    }

    @Test
    void shouldBeInvalidWhenTitleIsBlank() {

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        "",
                        VALID_DESCRIPTION,
                        VALID_STATUS,
                        VALID_PRIORITY,
                        VALID_DUE_DATE
                );

        Set<ConstraintViolation<UpdateTaskRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<UpdateTaskRequest> violation =
                violations.iterator().next();

        assertEquals(
                "title",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Title is required.",
                violation.getMessage()
        );
    }

    @Test
    void shouldBeInvalidWhenTitleContainsOnlySpaces() {

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        "   ",
                        VALID_DESCRIPTION,
                        VALID_STATUS,
                        VALID_PRIORITY,
                        VALID_DUE_DATE
                );

        Set<ConstraintViolation<UpdateTaskRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<UpdateTaskRequest> violation =
                violations.iterator().next();

        assertEquals(
                "title",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Title is required.",
                violation.getMessage()
        );
    }

    @Test
    void shouldBeValidWhenTitleHasMaximumAllowedLength() {

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        "a".repeat(100),
                        VALID_DESCRIPTION,
                        VALID_STATUS,
                        VALID_PRIORITY,
                        VALID_DUE_DATE
                );

        Set<ConstraintViolation<UpdateTaskRequest>> violations =
                validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalidWhenTitleExceedsMaximumLength() {

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        "a".repeat(101),
                        VALID_DESCRIPTION,
                        VALID_STATUS,
                        VALID_PRIORITY,
                        VALID_DUE_DATE
                );

        Set<ConstraintViolation<UpdateTaskRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<UpdateTaskRequest> violation =
                violations.iterator().next();

        assertEquals(
                "title",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Title must have at most 100 characters.",
                violation.getMessage()
        );
    }

    @Test
    void shouldBeValidWhenDescriptionHasMaximumAllowedLength() {

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        VALID_TITLE,
                        "a".repeat(500),
                        VALID_STATUS,
                        VALID_PRIORITY,
                        VALID_DUE_DATE
                );

        Set<ConstraintViolation<UpdateTaskRequest>> violations =
                validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalidWhenDescriptionExceedsMaximumLength() {

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        VALID_TITLE,
                        "a".repeat(501),
                        VALID_STATUS,
                        VALID_PRIORITY,
                        VALID_DUE_DATE
                );

        Set<ConstraintViolation<UpdateTaskRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<UpdateTaskRequest> violation =
                violations.iterator().next();

        assertEquals(
                "description",
                violation.getPropertyPath().toString()
        );

        assertEquals(
                "Description must have at most 500 characters.",
                violation.getMessage()
        );
    }

    @Test
    void shouldBeValidWhenDescriptionIsNull() {

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        VALID_TITLE,
                        null,
                        VALID_STATUS,
                        VALID_PRIORITY,
                        VALID_DUE_DATE
                );

        Set<ConstraintViolation<UpdateTaskRequest>> violations =
                validate(request);

        assertTrue(violations.isEmpty());
    }
}
