package com.example.resourceops.recommendation.dto;

public record ResourceRecommendationResponse(
        String status,
        String message,
        double cpuAverageUsage,
        double cpuRequest,
        double usageRate,
        String decision
) {
}