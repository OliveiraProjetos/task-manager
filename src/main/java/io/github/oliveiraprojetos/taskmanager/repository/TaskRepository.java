package io.github.oliveiraprojetos.taskmanager.repository;

import io.github.oliveiraprojetos.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findAllByUserId(UUID userId);

    Optional<Task> findByIdAndUserId(UUID taskId, UUID userId);
}