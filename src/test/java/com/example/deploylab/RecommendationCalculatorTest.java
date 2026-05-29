package com.example.deploylab;

import com.example.resourceops.recommendation.calculator.RecommendationCalculator;
import com.example.resourceops.recommendation.dto.ObservedMetricDto;
import com.example.resourceops.recommendation.dto.ResourceRequestDto;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.example.resourceops.recommendation.service.ResourceRecommendationService;

class RecommendationCalculatorTest {

    private final RecommendationCalculator calculator = new RecommendationCalculator();

    @Test
    void cpu_exceeds_upper_bound_should_be_capped() {
        ResourceRequestDto current = new ResourceRequestDto(100, 256);
        ObservedMetricDto observed = new ObservedMetricDto(500, 800, 400, 600, 0, 99);

        ResourceRequestDto result = calculator.calculate(current, observed);

        assertThat(result.cpuMillicores()).isLessThanOrEqualTo(200);
        assertThat(result.memoryMiB()).isLessThanOrEqualTo(512);
    }

    @Test
    void below_minimum_should_be_corrected_to_lower_bound() {
        ResourceRequestDto current = new ResourceRequestDto(100, 256);
        ObservedMetricDto observed = new ObservedMetricDto(10, 15, 50, 60, 0, 99);

        ResourceRequestDto result = calculator.calculate(current, observed);

        assertThat(result.cpuMillicores()).isGreaterThanOrEqualTo(50);
        assertThat(result.memoryMiB()).isGreaterThanOrEqualTo(128);
    }

    @Test
    void uptime_less_than_1h_should_return_HOLD() {
        ResourceRecommendationService service = new ResourceRecommendationService(null, null, null);
        assertThat(service.getQueryRange(0.5)).isEqualTo("HOLD");
        assertThat(service.getQueryRange(1.0)).isNotEqualTo("HOLD");
    }
}
