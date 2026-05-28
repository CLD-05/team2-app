package com.example.resourceops.recommendation.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity

@Getter

@Setter

@Builder

@NoArgsConstructor

@AllArgsConstructor

public class ResourceRecommendation {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

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

}