package com.example.resourceops.recommendation.dto;

import com.example.resourceops.recommendation.entity.ResourceRecommendation;

import lombok.Builder;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter

@Builder

public class ResourceRecommendationResponse {

    private Long id;

    private double cpuUsage;

    private double cpuRequest;

    private double cpuUsageRate;

    private double memoryUsageMb;

    private double memoryRequestMb;

    private double memoryUsageRate;

    private String cpuStatus;

    private String memoryStatus;

    private String recommendation;

    private LocalDateTime createdAt;

    public static ResourceRecommendationResponse from(ResourceRecommendation entity) {

        return ResourceRecommendationResponse.builder()

                .id(entity.getId())

                .cpuUsage(entity.getCpuUsage())

                .cpuRequest(entity.getCpuRequest())

                .cpuUsageRate(entity.getCpuUsageRate())

                .memoryUsageMb(entity.getMemoryUsageMb())

                .memoryRequestMb(entity.getMemoryRequestMb())

                .memoryUsageRate(entity.getMemoryUsageRate())

                .cpuStatus(entity.getCpuStatus())

                .memoryStatus(entity.getMemoryStatus())

                .recommendation(entity.getRecommendation())

                .createdAt(entity.getCreatedAt())

                .build();

    }

}