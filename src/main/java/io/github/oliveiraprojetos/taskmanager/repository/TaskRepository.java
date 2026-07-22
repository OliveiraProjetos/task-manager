package io.github.oliveiraprojetos.taskmanager.repository;

import io.github.oliveiraprojetos.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

}