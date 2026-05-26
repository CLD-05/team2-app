package com.example.resourceops.task.controller;

import com.example.resourceops.task.dto.TaskCreateRequest;
import com.example.resourceops.task.dto.TaskResponse;
import com.example.resourceops.task.dto.TaskUpdateRequest;
import com.example.resourceops.task.entity.TaskStatus;
import com.example.resourceops.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Task API", description = "작업(Task) 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Task 생성")
    @PostMapping
    public TaskResponse createTask(@Valid @RequestBody TaskCreateRequest request) {
        return taskService.createTask(request);
    }

    @Operation(summary = "Task 목록 조회")
    @GetMapping
    public List<TaskResponse> getTasks(
            @RequestParam(required = false) TaskStatus status
    ) {
        return taskService.getTasks(status);
    }

    @Operation(summary = "Task 단건 조회")
    @GetMapping("/{taskId}")
    public TaskResponse getTask(@PathVariable Long taskId) {
        return taskService.getTask(taskId);
    }

    @Operation(summary = "Task 수정")
    @PutMapping("/{taskId}")
    public TaskResponse updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        return taskService.updateTask(taskId, request);
    }

    @Operation(summary = "Task 삭제")
    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
    }
}