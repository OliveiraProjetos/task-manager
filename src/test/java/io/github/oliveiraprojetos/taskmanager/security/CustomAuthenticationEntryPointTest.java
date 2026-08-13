package io.github.oliveiraprojetos.taskmanager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authenticationException;

    @Test
    void shouldReturnUnauthorizedJsonWhenAuthenticationIsRequired() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        CustomAuthenticationEntryPoint entryPoint =
                new CustomAuthenticationEntryPoint(objectMapper);

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        when(request.getRequestURI())
                .thenReturn("/tasks");

        when(response.getOutputStream())
                .thenReturn(new jakarta.servlet.ServletOutputStream() {

                    @Override
                    public void write(int b) {
                        outputStream.write(b);
                    }

                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(
                            jakarta.servlet.WriteListener writeListener
                    ) {
                    }
                });

        entryPoint.commence(
                request,
                response,
                authenticationException
        );

        verify(response)
                .setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        verify(response)
                .setContentType("application/json");

        String json = outputStream.toString();

        assertNotNull(json);
        assertEquals(
                "Unauthorized",
                objectMapper.readTree(json)
                        .get("error")
                        .asText()
        );

        assertEquals(
                "Authentication is required.",
                objectMapper.readTree(json)
                        .get("message")
                        .asText()
        );

        assertEquals(
                "/tasks",
                objectMapper.readTree(json)
                        .get("path")
                        .asText()
        );
    }
}