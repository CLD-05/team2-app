package com.example.resourceops.recommendation.calculator;

import com.example.resourceops.recommendation.config.AwsCostProperties;
import com.example.resourceops.recommendation.config.AwsPricingProperties;
import com.example.resourceops.recommendation.dto.AwsTrafficUsageDto;
import com.example.resourceops.recommendation.dto.CostComponent;
import com.example.resourceops.recommendation.dto.CostResponseDto;
import com.example.resourceops.recommendation.dto.ResourceCostType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TrafficCostCalculator {

    private static final double HOURS_PER_MONTH = 730.0;

    private final AwsCostProperties awsCostProperties;
    private final AwsPricingProperties awsPricingProperties;

    public List<CostResponseDto> calculate(ResourceCostType type, AwsTrafficUsageDto trafficUsage) {
        return List.of(
                calculateAlbCost(type, trafficUsage),
                calculateNatGatewayCost(type, trafficUsage),
                calculateDataTransferCost(type, trafficUsage)
        );
    }

    private CostResponseDto calculateAlbCost(ResourceCostType type, AwsTrafficUsageDto trafficUsage) {
        double intervalHours = awsCostProperties.getPollingMinutes() / 60.0;
        double intervalCost = awsPricingProperties.getAlbHourUsd() * intervalHours
                + awsPricingProperties.getAlbLcuHourUsd() * trafficUsage.albConsumedLcuHours();
        double hourlyCost = intervalCost / intervalHours;

        return response(type, CostComponent.ALB, hourlyCost);
    }

    private CostResponseDto calculateNatGatewayCost(ResourceCostType type, AwsTrafficUsageDto trafficUsage) {
        double intervalHours = awsCostProperties.getPollingMinutes() / 60.0;
        double intervalCost = awsPricingProperties.getNatGatewayHourUsd() * intervalHours
                + awsPricingProperties.getNatGatewayDataGbUsd() * trafficUsage.natGatewayProcessedGiB();
        double hourlyCost = intervalCost / intervalHours;

        return response(type, CostComponent.NAT_GATEWAY, hourlyCost);
    }

    private CostResponseDto calculateDataTransferCost(ResourceCostType type, AwsTrafficUsageDto trafficUsage) {
        double intervalHours = awsCostProperties.getPollingMinutes() / 60.0;
        double intervalCost = awsPricingProperties.getDataTransferOutGbUsd() * trafficUsage.dataTransferOutGiB();
        double hourlyCost = intervalHours == 0.0 ? 0.0 : intervalCost / intervalHours;

        return response(type, CostComponent.DATA_TRANSFER, hourlyCost);
    }

    private CostResponseDto response(ResourceCostType type, CostComponent costComponent, double hourlyCost) {
        return new CostResponseDto(
                type.name(),
                costComponent.name(),
                "AWS_MANAGED",
                "ON_DEMAND",
                0.0,
                0.0,
                "TRAFFIC",
                round(hourlyCost),
                round(hourlyCost * HOURS_PER_MONTH)
        );
    }

    private double round(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}
