package org.example.toyapsback.repository;

import org.example.toyapsback.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, String> {
}
