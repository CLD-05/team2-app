package com.example.resourceops.recommendation.dto;

public record CostResponseDto(
        String type,
        double cpuCostPerMonthUsd,
        double memoryCostPerMonthUsd,
        double totalCostPerMonthUsd
) {
}
