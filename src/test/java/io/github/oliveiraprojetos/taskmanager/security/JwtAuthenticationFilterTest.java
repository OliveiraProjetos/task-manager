package io.github.oliveiraprojetos.taskmanager.security;

import io.github.oliveiraprojetos.taskmanager.model.User;
import io.github.oliveiraprojetos.taskmanager.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private static final String TOKEN = "token123";
    private static final String USERNAME = "teste";

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsNotBearer() throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn("Basic abc123");

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }

    @Test
    void shouldContinueFilterChainWhenTokenIsInvalid() throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + TOKEN);

        when(jwtService.isTokenValid(TOKEN))
                .thenReturn(false);

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(jwtService)
                .isTokenValid(TOKEN);

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(userDetailsService);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid() throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + TOKEN);

        when(jwtService.isTokenValid(TOKEN))
                .thenReturn(true);

        when(jwtService.extractUsername(TOKEN))
                .thenReturn(USERNAME);

        User user = new User(
                USERNAME,
                "senhaCriptografada"
        );

        when(userDetailsService.loadUserByUsername(USERNAME))
                .thenReturn(user);

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(jwtService)
                .isTokenValid(TOKEN);

        verify(jwtService)
                .extractUsername(TOKEN);

        verify(userDetailsService)
                .loadUserByUsername(USERNAME);

        verify(filterChain)
                .doFilter(request, response);

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertEquals(user, authentication.getPrincipal());
        assertEquals(
                USERNAME,
                authentication.getName()
        );
        assertEquals(
                true,
                authentication.isAuthenticated()
        );
    }

    @Test
    void shouldContinueFilterChainWhenTokenUserDoesNotExist() throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + TOKEN);

        when(jwtService.isTokenValid(TOKEN))
                .thenReturn(true);

        when(jwtService.extractUsername(TOKEN))
                .thenReturn(USERNAME);

        when(userDetailsService.loadUserByUsername(USERNAME))
                .thenThrow(
                        new org.springframework.security.core.userdetails
                                .UsernameNotFoundException(USERNAME)
                );

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(jwtService)
                .isTokenValid(TOKEN);

        verify(jwtService)
                .extractUsername(TOKEN);

        verify(userDetailsService)
                .loadUserByUsername(USERNAME);

        verify(filterChain)
                .doFilter(request, response);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }
}