package com.example.resourceops.task.dto;

import com.example.resourceops.task.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskUpdateRequest(

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이하로 입력해주세요.")
        String title,

        @Size(max = 1000, message = "설명은 1000자 이하로 입력해주세요.")
        String description,

        TaskStatus status,

        LocalDateTime dueDate
) {
}