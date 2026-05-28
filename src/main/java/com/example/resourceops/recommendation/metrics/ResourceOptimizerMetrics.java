package com.example.resourceops.recommendation.metrics;

import com.example.resourceops.recommendation.dto.CostResponseDto;
import com.example.resourceops.recommendation.dto.ResourceCostType;
import com.example.resourceops.recommendation.dto.ResourceRequestDto;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ResourceOptimizerMetrics {

    private final Map<ResourceCostType, MetricValues> values = new EnumMap<>(ResourceCostType.class);

    public ResourceOptimizerMetrics(MeterRegistry meterRegistry) {
        for (ResourceCostType type : ResourceCostType.values()) {
            MetricValues metricValues = new MetricValues();
            values.put(type, metricValues);
            registerGauges(meterRegistry, type, metricValues);
        }
    }

    public void publish(ResourceCostType type, ResourceRequestDto resourceRequest, CostResponseDto cost) {
        MetricValues metricValues = values.get(type);
        metricValues.cpuRequestMillicores.set((double) resourceRequest.cpuMillicores());
        metricValues.memoryRequestMiB.set((double) resourceRequest.memoryMiB());
        metricValues.monthlyCostUsd.set(cost.totalCostPerMonthUsd());
    }

    private void registerGauges(MeterRegistry meterRegistry, ResourceCostType type, MetricValues metricValues) {
        Gauge.builder("resource_optimizer_cpu_request_millicores", metricValues.cpuRequestMillicores, AtomicReference::get)
                .description("Current or recommended Kubernetes CPU request in millicores")
                .tag("type", type.name())
                .register(meterRegistry);

        Gauge.builder("resource_optimizer_memory_request_mib", metricValues.memoryRequestMiB, AtomicReference::get)
                .description("Current or recommended Kubernetes memory request in MiB")
                .tag("type", type.name())
                .register(meterRegistry);

        Gauge.builder("resource_optimizer_cost_monthly_usd", metricValues.monthlyCostUsd, AtomicReference::get)
                .description("Estimated monthly request-based resource cost in USD")
                .tag("type", type.name())
                .register(meterRegistry);
    }

    private static class MetricValues {
        private final AtomicReference<Double> cpuRequestMillicores = new AtomicReference<>(0.0);
        private final AtomicReference<Double> memoryRequestMiB = new AtomicReference<>(0.0);
        private final AtomicReference<Double> monthlyCostUsd = new AtomicReference<>(0.0);
    }
}
