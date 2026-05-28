package com.example.resourceops.recommendation.service;

import com.example.resourceops.prometheus.PrometheusClient;
import com.example.resourceops.recommendation.calculator.CostCalculator;
import com.example.resourceops.recommendation.calculator.RecommendationCalculator;
import com.example.resourceops.recommendation.dto.CostResponseDto;
import com.example.resourceops.recommendation.dto.ObservedMetricDto;
import com.example.resourceops.recommendation.dto.OptimizationCompareResponseDto;
import com.example.resourceops.recommendation.dto.PricingModel;
import com.example.resourceops.recommendation.dto.ResourceCostType;
import com.example.resourceops.recommendation.dto.ResourceRecommendationResponse;
import com.example.resourceops.recommendation.dto.ResourceRequestDto;
import com.example.resourceops.recommendation.entity.ResourceRecommendation;
import com.example.resourceops.recommendation.metrics.ResourceOptimizerMetrics;
import com.example.resourceops.recommendation.repository.ResourceRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResourceRecommendationService {

    private final CostCalculator costCalculator;
    private final RecommendationCalculator recommendationCalculator;
    private final ResourceOptimizerMetrics resourceOptimizerMetrics;

    private final PrometheusClient prometheusClient;
    private final ResourceRecommendationRepository repository;

    public OptimizationCompareResponseDto compare(
            ResourceRequestDto currentRequest,
            ObservedMetricDto observedMetric,
            String instanceType,
            PricingModel pricingModel
    ) {
        CostResponseDto currentCost = costCalculator.calculate(
                ResourceCostType.CURRENT,
                currentRequest,
                instanceType,
                pricingModel
        );

        ResourceRequestDto recommendedRequest =
                recommendationCalculator.calculate(currentRequest, observedMetric);

        CostResponseDto recommendedCost = costCalculator.calculate(
                ResourceCostType.RECOMMENDED,
                recommendedRequest,
                instanceType,
                pricingModel
        );

        resourceOptimizerMetrics.publish(ResourceCostType.CURRENT, currentRequest, currentCost);
        resourceOptimizerMetrics.publish(ResourceCostType.RECOMMENDED, recommendedRequest, recommendedCost);

        double savings = currentCost.monthlyCostUsd() - recommendedCost.monthlyCostUsd();
        double savingsPercent = currentCost.monthlyCostUsd() == 0.0
                ? 0.0
                : savings / currentCost.monthlyCostUsd() * 100.0;

        return new OptimizationCompareResponseDto(
                currentRequest,
                observedMetric,
                recommendedRequest,
                currentCost,
                recommendedCost,
                round4(savings),
                round4(savingsPercent)
        );
    }

    public CostResponseDto calculateCurrentCost(
            ResourceRequestDto currentRequest,
            String instanceType,
            PricingModel pricingModel
    ) {
        CostResponseDto currentCost = costCalculator.calculate(
                ResourceCostType.CURRENT,
                currentRequest,
                instanceType,
                pricingModel
        );

        resourceOptimizerMetrics.publish(ResourceCostType.CURRENT, currentRequest, currentCost);

        return currentCost;
    }

    public ResourceRecommendationResponse analyze(double cpuRequest, double memoryRequestMb) {
        double cpuUsage = getCpuUsage();
        double memoryUsageMb = getMemoryUsageMb();

        double cpuUsageRate = calculateRate(cpuUsage, cpuRequest);
        double memoryUsageRate = calculateRate(memoryUsageMb, memoryRequestMb);

        String cpuStatus = analyzeStatus(cpuUsageRate, "CPU");
        String memoryStatus = analyzeStatus(memoryUsageRate, "Memory");
        String recommendation = makeRecommendation(cpuUsageRate, memoryUsageRate);

        ResourceRecommendation result = ResourceRecommendation.builder()
                .cpuUsage(round2(cpuUsage))
                .cpuRequest(round2(cpuRequest))
                .cpuUsageRate(round2(cpuUsageRate))
                .memoryUsageMb(round2(memoryUsageMb))
                .memoryRequestMb(round2(memoryRequestMb))
                .memoryUsageRate(round2(memoryUsageRate))
                .cpuStatus(cpuStatus)
                .memoryStatus(memoryStatus)
                .recommendation(recommendation)
                .createdAt(LocalDateTime.now())
                .build();

        ResourceRecommendation saved = repository.save(result);

        return ResourceRecommendationResponse.from(saved);
    }

    private double getCpuUsage() {
        return prometheusClient.queryValue("system_cpu_usage");
    }

    private double getMemoryUsageMb() {
        double bytes = prometheusClient.queryValue("sum(jvm_memory_used_bytes{area=\"heap\"})");
        return bytes / 1024 / 1024;
    }

    private double calculateRate(double usage, double request) {
        if (request <= 0) {
            return 0.0;
        }

        return usage / request * 100;
    }

    private String analyzeStatus(double usageRate, String type) {
        if (usageRate < 30) {
            return type + " Request 대비 사용률이 30% 미만으로 과할당 가능성이 높습니다.";
        }

        if (usageRate >= 80) {
            return type + " Request 대비 사용률이 80% 이상으로 리소스 부족 가능성이 있습니다.";
        }

        return type + " 사용률이 적정 범위입니다.";
    }

    private String makeRecommendation(double cpuUsageRate, double memoryUsageRate) {
        boolean cpuOverAllocated = cpuUsageRate < 30;
        boolean memoryOverAllocated = memoryUsageRate < 30;
        boolean cpuUnderAllocated = cpuUsageRate >= 80;
        boolean memoryUnderAllocated = memoryUsageRate >= 80;

        if (cpuOverAllocated || memoryOverAllocated) {
            return "현재 Request 대비 실제 사용량이 낮아 리소스 축소를 검토할 수 있습니다.";
        }

        if (cpuUnderAllocated || memoryUnderAllocated) {
            return "현재 사용률이 높아 Request 증설을 검토해야 합니다.";
        }

        return "현재 리소스 사용량은 적정 범위입니다.";
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double round4(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
    public String getQueryRange(double prometheusRunningHours) {
        if (prometheusRunningHours < 1) {
            return "5m";
        }

        if (prometheusRunningHours < 12) {
            return "1h";
        }

        return "12h";
    }
}