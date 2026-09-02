package io.github.oliveiraprojetos.taskmanager.exception;

import io.github.oliveiraprojetos.taskmanager.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private static final String PATH = "/tasks";
    private static final String USERNAME = "teste";

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    private HttpServletRequest request() {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI(PATH);

        return request;
    }

    private MethodParameter methodParameter()
            throws NoSuchMethodException {

        return new MethodParameter(
                GlobalExceptionHandlerTest.class
                        .getDeclaredMethod(
                                "dummyMethod",
                                String.class
                        ),
                0
        );
    }

    @SuppressWarnings("unused")
    private void dummyMethod(String value) {
    }

    @Test
    void shouldHandleInvalidCredentialsException() {

        InvalidCredentialsException exception =
                new InvalidCredentialsException();

        ApiError error =
                handler.handleInvalidCredentials(
                        exception,
                        request()
                );

        assertNotNull(error.timestamp());

        assertEquals(
                401,
                error.status()
        );

        assertEquals(
                "Unauthorized",
                error.error()
        );

        assertEquals(
                "Invalid credentials.",
                error.message()
        );

        assertEquals(
                PATH,
                error.path()
        );
    }

    @Test
    void shouldHandleTaskNotFoundException() {

        TaskNotFoundException exception =
                new TaskNotFoundException(
                        java.util.UUID.fromString(
                                "11111111-1111-1111-1111-111111111111"
                        )
                );

        ApiError error =
                handler.handleTaskNotFound(
                        exception,
                        request()
                );

        assertNotNull(error.timestamp());

        assertEquals(
                404,
                error.status()
        );

        assertEquals(
                "Not Found",
                error.error()
        );

        assertEquals(
                exception.getMessage(),
                error.message()
        );

        assertEquals(
                PATH,
                error.path()
        );
    }

    @Test
    void shouldHandleUserNotFoundException() {

        UserNotFoundException exception =
                new UserNotFoundException(USERNAME);

        ApiError error =
                handler.handleUserNotFound(
                        exception,
                        request()
                );

        assertNotNull(error.timestamp());

        assertEquals(
                404,
                error.status()
        );

        assertEquals(
                "Not Found",
                error.error()
        );

        assertEquals(
                exception.getMessage(),
                error.message()
        );

        assertEquals(
                PATH,
                error.path()
        );
    }

    @Test
    void shouldHandleUsernameAlreadyExistsException() {

        UsernameAlreadyExistsException exception =
                new UsernameAlreadyExistsException(USERNAME);

        ApiError error =
                handler.handleUsernameAlreadyExists(
                        exception,
                        request()
                );

        assertNotNull(error.timestamp());

        assertEquals(
                409,
                error.status()
        );

        assertEquals(
                "Conflict",
                error.error()
        );

        assertEquals(
                exception.getMessage(),
                error.message()
        );

        assertEquals(
                PATH,
                error.path()
        );
    }

    @Test
    void shouldHandleValidationException() throws NoSuchMethodException {

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(
                        new Object(),
                        "request"
                );

        bindingResult.addError(
                new FieldError(
                        "request",
                        "username",
                        "Username is required."
                )
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        methodParameter(),
                        bindingResult
                );

        ApiError error =
                handler.handleValidation(
                        exception,
                        request()
                );

        assertNotNull(error.timestamp());

        assertEquals(
                400,
                error.status()
        );

        assertEquals(
                "Bad Request",
                error.error()
        );

        assertEquals(
                "Username is required.",
                error.message()
        );

        assertEquals(
                PATH,
                error.path()
        );
    }

    @Test
    void shouldHandleGenericException() {

        Exception exception =
                new RuntimeException("Unexpected error.");

        ApiError error =
                handler.handleGenericException(
                        exception,
                        request()
                );

        assertNotNull(error.timestamp());

        assertEquals(
                500,
                error.status()
        );

        assertEquals(
                "Internal Server Error",
                error.error()
        );

        assertEquals(
                "Internal Server Error.",
                error.message()
        );

        assertEquals(
                PATH,
                error.path()
        );
    }

    @Test
    void shouldUseDefaultMessageWhenValidationHasNoFieldErrors()
            throws NoSuchMethodException {

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(
                        new Object(),
                        "request"
                );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        methodParameter(),
                        bindingResult
                );

        ApiError error =
                handler.handleValidation(
                        exception,
                        request()
                );

        assertNotNull(error.timestamp());

        assertEquals(
                400,
                error.status()
        );

        assertEquals(
                "Bad Request",
                error.error()
        );

        assertEquals(
                "Validation Error.",
                error.message()
        );

        assertEquals(
                PATH,
                error.path()
        );
    }
}
