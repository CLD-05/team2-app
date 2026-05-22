package com.example.resourceops.task.repository;

import com.example.resourceops.task.entity.Task;
import com.example.resourceops.task.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);
}