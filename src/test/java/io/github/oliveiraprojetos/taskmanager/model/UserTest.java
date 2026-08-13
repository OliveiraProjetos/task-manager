package io.github.oliveiraprojetos.taskmanager.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private static final String USERNAME = "teste";
    private static final String PASSWORD = "123456";

    @Test
    void shouldCreateUserWhenUsernameAndPasswordAreProvided() {

        User user = new User(USERNAME, PASSWORD);

        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenUserHasNoRoles() {

        User user = new User(USERNAME, PASSWORD);

        Collection<? extends GrantedAuthority> authorities =
                user.getAuthorities();

        assertTrue(authorities.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenAccountIsNotExpired() {

        User user = new User(USERNAME, PASSWORD);

        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void shouldReturnTrueWhenAccountIsNotLocked() {

        User user = new User(USERNAME, PASSWORD);

        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void shouldReturnTrueWhenCredentialsAreNotExpired() {

        User user = new User(USERNAME, PASSWORD);

        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void shouldReturnTrueWhenUserIsEnabled() {

        User user = new User(USERNAME, PASSWORD);

        assertTrue(user.isEnabled());
    }
}