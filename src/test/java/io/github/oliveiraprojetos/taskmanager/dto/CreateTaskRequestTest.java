package io.github.oliveiraprojetos.taskmanager.dto;

import io.github.oliveiraprojetos.taskmanager.enums.TaskPriority;
import io.github.oliveiraprojetos.taskmanager.enums.TaskStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateTaskRequestTest {

    private static final String VALID_TITLE = "Título válido";
    private static final String VALID_DESCRIPTION = "Descrição válida.";

    private static final TaskStatus STATUS =
            TaskStatus.PENDING;

    private static final TaskPriority PRIORITY =
            TaskPriority.MEDIUM;

    private static final LocalDate DUE_DATE =
            LocalDate.of(2026, 12, 30);

    private static final Validator validator =
            ValidationTestUtils.validator();

    private CreateTaskRequest createTaskRequest() {
        return new CreateTaskRequest(
                VALID_TITLE,
                VALID_DESCRIPTION,
                STATUS,
                PRIORITY,
                DUE_DATE
        );
    }

    private Set<ConstraintViolation<CreateTaskRequest>> validate(
            CreateTaskRequest request
    ) {
        return validator.validate(request);
    }

    @Test
    void shouldBeValidWhenTitleAndDescriptionAreValid() {

        CreateTaskRequest request = createTaskRequest();

        Set<ConstraintViolation<CreateTaskRequest>> violations =
                validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalidWhenTitleIsNull() {

        CreateTaskRequest request =
                new CreateTaskRequest(
                        null,
                        VALID_DESCRIPTION,
                        STATUS,
                        PRIORITY,
                        DUE_DATE
                );

        Set<ConstraintViolation<CreateTaskRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<CreateTaskRequest> violation =
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

        CreateTaskRequest request =
                new CreateTaskRequest(
                        "",
                        VALID_DESCRIPTION,
                        STATUS,
                        PRIORITY,
                        DUE_DATE
                );

        Set<ConstraintViolation<CreateTaskRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<CreateTaskRequest> violation =
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

        CreateTaskRequest request =
                new CreateTaskRequest(
                        "   ",
                        VALID_DESCRIPTION,
                        STATUS,
                        PRIORITY,
                        DUE_DATE
                );

        Set<ConstraintViolation<CreateTaskRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<CreateTaskRequest> violation =
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

        CreateTaskRequest request =
                new CreateTaskRequest(
                        "a".repeat(100),
                        VALID_DESCRIPTION,
                        STATUS,
                        PRIORITY,
                        DUE_DATE
                );

        Set<ConstraintViolation<CreateTaskRequest>> violations =
                validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalidWhenTitleExceedsMaximumLength() {

        CreateTaskRequest request =
                new CreateTaskRequest(
                        "a".repeat(101),
                        VALID_DESCRIPTION,
                        STATUS,
                        PRIORITY,
                        DUE_DATE
                );

        Set<ConstraintViolation<CreateTaskRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<CreateTaskRequest> violation =
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

        CreateTaskRequest request =
                new CreateTaskRequest(
                        VALID_TITLE,
                        "a".repeat(500),
                        STATUS,
                        PRIORITY,
                        DUE_DATE
                );

        Set<ConstraintViolation<CreateTaskRequest>> violations =
                validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalidWhenDescriptionExceedsMaximumLength() {

        CreateTaskRequest request =
                new CreateTaskRequest(
                        VALID_TITLE,
                        "a".repeat(501),
                        STATUS,
                        PRIORITY,
                        DUE_DATE
                );

        Set<ConstraintViolation<CreateTaskRequest>> violations =
                validate(request);

        assertEquals(1, violations.size());

        ConstraintViolation<CreateTaskRequest> violation =
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

        CreateTaskRequest request =
                new CreateTaskRequest(
                        VALID_TITLE,
                        null,
                        STATUS,
                        PRIORITY,
                        DUE_DATE
                );

        Set<ConstraintViolation<CreateTaskRequest>> violations =
                validate(request);

        assertTrue(violations.isEmpty());
    }
}
