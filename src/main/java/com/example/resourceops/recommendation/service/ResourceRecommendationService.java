package com.example.resourceops.recommendation.service;

import com.example.resourceops.recommendation.calculator.CostCalculator;
import com.example.resourceops.recommendation.calculator.RecommendationCalculator;
import com.example.resourceops.recommendation.dto.CostResponseDto;
import com.example.resourceops.recommendation.dto.ObservedMetricDto;
import com.example.resourceops.recommendation.dto.OptimizationCompareResponseDto;
import com.example.resourceops.recommendation.dto.PricingModel;
import com.example.resourceops.recommendation.dto.ResourceCostType;
import com.example.resourceops.recommendation.dto.ResourceRequestDto;
import com.example.resourceops.recommendation.dto.TotalCostSummaryDto;
import com.example.resourceops.recommendation.metrics.ResourceOptimizerMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceRecommendationService {

    private final CostCalculator costCalculator;
    private final RecommendationCalculator recommendationCalculator;
    private final ResourceOptimizerMetrics resourceOptimizerMetrics;
    private final AwsCurrentCostService awsCurrentCostService;

    public OptimizationCompareResponseDto compare(
            ResourceRequestDto currentRequest,
            ObservedMetricDto observedMetric,
            String instanceType,
            PricingModel pricingModel
    ) {
        // Instance 현재/추천 비용 계산
        CostResponseDto currentCost = costCalculator.calculate(
                ResourceCostType.CURRENT,
                currentRequest,
                instanceType,
                pricingModel
        );
        ResourceRequestDto recommendedRequest = recommendationCalculator.calculate(currentRequest, observedMetric);
        CostResponseDto recommendedCost = costCalculator.calculate(
                ResourceCostType.RECOMMENDED,
                recommendedRequest,
                instanceType,
                pricingModel
        );

        resourceOptimizerMetrics.publish(ResourceCostType.CURRENT, currentRequest, currentCost);
        resourceOptimizerMetrics.publish(ResourceCostType.RECOMMENDED, recommendedRequest, recommendedCost);

        // ALB / NAT / Transfer 비용 (현재값 = 추천값 동일)
        double albCost = awsCurrentCostService.getAlbMonthlyCost();
        double natCost = awsCurrentCostService.getNatGatewayMonthlyCost();
        double transferCost = awsCurrentCostService.getDataTransferMonthlyCost();

        // 총 비용 계산
        TotalCostSummaryDto currentTotalCost = TotalCostSummaryDto.of(
                albCost,
                natCost,
                transferCost,
                currentCost.monthlyCostUsd()
        );

        TotalCostSummaryDto recommendedTotalCost = TotalCostSummaryDto.of(
                albCost,
                natCost,
                transferCost,
                recommendedCost.monthlyCostUsd()
        );

        double savings = currentTotalCost.totalCostUsd() - recommendedTotalCost.totalCostUsd();
        double savingsPercent = currentTotalCost.totalCostUsd() == 0.0
                ? 0.0
                : savings / currentTotalCost.totalCostUsd() * 100.0;

        return new OptimizationCompareResponseDto(
                currentRequest,
                observedMetric,
                recommendedRequest,
                currentCost,
                recommendedCost,
                currentTotalCost,
                recommendedTotalCost,
                round(savings),
                round(savingsPercent)
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

    public String getQueryRange(double prometheusRunningHours) {
        if (prometheusRunningHours < 1) {
            return "HOLD";
        }

        if (prometheusRunningHours < 12) {
            return prometheusRunningHours + "h";
        }

        return "12h";
    }

    private double round(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}