package io.github.oliveiraprojetos.taskmanager.repository;

import io.github.oliveiraprojetos.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

}