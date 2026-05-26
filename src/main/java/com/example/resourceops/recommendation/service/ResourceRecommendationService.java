package com.example.resourceops.recommendation.service;

import com.example.resourceops.recommendation.dto.ResourceRecommendationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceRecommendationService {

    private static final double OVER_ALLOCATED_THRESHOLD = 0.30;

    public ResourceRecommendationResponse recommendCpuResource(
            double prometheusRunningHours,
            double cpuAverageUsage,
            double cpuRequest
    ) {
        if (prometheusRunningHours < 1) {
            return new ResourceRecommendationResponse(
                    "HOLD",
                    "Prometheus 수집 시간이 1시간 미만이므로 추천을 보류합니다.",
                    cpuAverageUsage,
                    cpuRequest,
                    0.0,
                    "추천 보류"
            );
        }

        if (cpuRequest <= 0) {
            throw new IllegalArgumentException("CPU request 값은 0보다 커야 합니다.");
        }

        double usageRate = cpuAverageUsage / cpuRequest;

        if (usageRate < OVER_ALLOCATED_THRESHOLD) {
            return new ResourceRecommendationResponse(
                    "OVER_ALLOCATED",
                    "CPU 평균 사용량이 request의 30% 미만이므로 과할당 가능성이 높습니다.",
                    cpuAverageUsage,
                    cpuRequest,
                    usageRate,
                    "CPU request 감소 추천"
            );
        }

        return new ResourceRecommendationResponse(
                "NORMAL",
                "CPU 평균 사용량이 적정 범위입니다.",
                cpuAverageUsage,
                cpuRequest,
                usageRate,
                "유지"
        );
    }

    public String getQueryRange(double prometheusRunningHours) {
        if (prometheusRunningHours < 1) {
            return "HOLD";
        }

        if (prometheusRunningHours < 12) {
            return prometheusRunningHours + "h";
        }

        return "12h";
    }
}