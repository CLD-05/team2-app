package com.example.resourceops.recommendation.dto;

public record TotalCostSummaryDto(
        double albCostUsd,
        double natGatewayCostUsd,
        double dataTransferCostUsd,
        double instanceCostUsd,
        double totalCostUsd
) {
    public static TotalCostSummaryDto of(
            double albCost,
            double natGatewayCost,
            double dataTransferCost,
            double instanceCost
    ) {
        double total = albCost + natGatewayCost + dataTransferCost + instanceCost;
        return new TotalCostSummaryDto(
                round(albCost),
                round(natGatewayCost),
                round(dataTransferCost),
                round(instanceCost),
                round(total)
        );
    }

    private static double round(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}