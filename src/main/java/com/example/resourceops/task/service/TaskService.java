package com.example.resourceops.task.service;

import com.example.resourceops.task.dto.TaskCreateRequest;
import com.example.resourceops.task.dto.TaskResponse;
import com.example.resourceops.task.dto.TaskUpdateRequest;
import com.example.resourceops.task.entity.Task;
import com.example.resourceops.task.entity.TaskStatus;
import com.example.resourceops.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {
        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status() == null ? TaskStatus.TODO : request.status())
                .dueDate(request.dueDate())
                .build();

        Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    public List<TaskResponse> getTasks(TaskStatus status) {
        if (status == null) {
            return taskRepository.findAll()
                    .stream()
                    .map(TaskResponse::from)
                    .toList();
        }

        return taskRepository.findByStatus(status)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    public TaskResponse getTask(Long taskId) {
        Task task = findTaskById(taskId);
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = findTaskById(taskId);

        task.update(
                request.title(),
                request.description(),
                request.status(),
                request.dueDate()
        );

        return TaskResponse.from(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task task = findTaskById(taskId);
        taskRepository.delete(task);
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Task입니다. id=" + taskId));
    }
}