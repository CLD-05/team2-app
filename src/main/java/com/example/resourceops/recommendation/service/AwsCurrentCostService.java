package com.example.resourceops.recommendation.service;

import com.example.resourceops.recommendation.dto.CostComponent;
import com.example.resourceops.recommendation.dto.ResourceCostType;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AwsCurrentCostService {

    private final MeterRegistry meterRegistry;

    public double getAlbCost() {
        return getHourlyCost(ResourceCostType.CURRENT, CostComponent.ALB);
    }

    public double getNatGatewayCost() {
        return getHourlyCost(ResourceCostType.CURRENT, CostComponent.NAT_GATEWAY);
    }

    public double getDataTransferCost() {
        return getHourlyCost(ResourceCostType.CURRENT, CostComponent.DATA_TRANSFER);
    }

    // 월 비용으로 변환 (hourly * 730)
    public double getAlbMonthlyCost() {
        return getAlbCost() * 730.0;
    }

    public double getNatGatewayMonthlyCost() {
        return getNatGatewayCost() * 730.0;
    }

    public double getDataTransferMonthlyCost() {
        return getDataTransferCost() * 730.0;
    }

    private double getHourlyCost(ResourceCostType type, CostComponent component) {
        try {
            return meterRegistry.get("resource_optimizer_cost_hourly_usd")
                    .tag("type", type.name())
                    .tag("cost_component", component.name())
                    .gauge()
                    .value();
        } catch (Exception e) {
            return 0.0;
        }
    }
}