package com.example.resourceops.recommendation.controller;

import com.example.resourceops.recommendation.dto.ResourceRecommendationResponse;
import com.example.resourceops.recommendation.service.ResourceRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class ResourceRecommendationController {

    private final ResourceRecommendationService recommendationService;

    @Operation(summary = "CPU 리소스 추천")
    @GetMapping("/cpu")
    public ResourceRecommendationResponse recommendCpu(
            @RequestParam double prometheusRunningHours,
            @RequestParam double cpuAverageUsage,
            @RequestParam double cpuRequest
    ) {
        return recommendationService.recommendCpuResource(
                prometheusRunningHours,
                cpuAverageUsage,
                cpuRequest
        );
    }

    @Operation(summary = "Prometheus 평균 계산 기준 시간 조회")
    @GetMapping("/query-range")
    public String getQueryRange(
            @RequestParam double prometheusRunningHours
    ) {
        return recommendationService.getQueryRange(prometheusRunningHours);
    }
}