package com.example.resourceops.recommendation.calculator;

import com.example.resourceops.recommendation.dto.CostResponseDto;
import com.example.resourceops.recommendation.dto.ResourceCostType;
import com.example.resourceops.recommendation.dto.ResourceRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CostCalculator {

    private static final double HOURS_PER_MONTH = 730.0;

    private final double cpuCoreHourUsd;
    private final double memoryGiBHourUsd;

    public CostCalculator(
            @Value("${resource-optimizer.cost.cpu-core-hour-usd:0.0416}") double cpuCoreHourUsd,
            @Value("${resource-optimizer.cost.memory-gib-hour-usd:0.0052}") double memoryGiBHourUsd
    ) {
        this.cpuCoreHourUsd = cpuCoreHourUsd;
        this.memoryGiBHourUsd = memoryGiBHourUsd;
    }

    public CostResponseDto calculate(ResourceCostType type, ResourceRequestDto resourceRequest) {
        validate(resourceRequest);

        double cpuCores = resourceRequest.cpuMillicores() / 1000.0;
        double memoryGiB = resourceRequest.memoryMiB() / 1024.0;

        double cpuCost = cpuCores * cpuCoreHourUsd * HOURS_PER_MONTH;
        double memoryCost = memoryGiB * memoryGiBHourUsd * HOURS_PER_MONTH;
        double totalCost = cpuCost + memoryCost;

        return new CostResponseDto(
                type.name(),
                round(cpuCost),
                round(memoryCost),
                round(totalCost)
        );
    }

    private void validate(ResourceRequestDto resourceRequest) {
        if (resourceRequest.cpuMillicores() < 0 || resourceRequest.memoryMiB() < 0) {
            throw new IllegalArgumentException("Resource request values must be greater than or equal to zero.");
        }
    }

    private double round(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}
