package com.example.resourceops.analysis;

import com.example.resourceops.prometheus.PrometheusClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceAnalysisService {

    private final PrometheusClient prometheusClient;

    public ResourceAnalysisResult analyzeCpu(String namespace) {

        String cpuUsageQuery = """
            sum(
              avg_over_time(
                rate(container_cpu_usage_seconds_total{namespace="%s", container!="", pod!=""}[5m])[12h:5m]
              )
            )
            """.formatted(namespace);

        String cpuRequestQuery = """
            sum(
              kube_pod_container_resource_requests{namespace="%s", resource="cpu"}
            )
            """.formatted(namespace);

        double avgCpuUsage = prometheusClient.queryValue(cpuUsageQuery);
        double cpuRequest = prometheusClient.queryValue(cpuRequestQuery);

        return calculateResult("CPU", avgCpuUsage, cpuRequest);
    }

    public ResourceAnalysisResult analyzeMemory(String namespace) {

        String memoryUsageQuery = """
            sum(
              avg_over_time(
                container_memory_working_set_bytes{namespace="%s", container!="", pod!=""}[12h]
              )
            )
            """.formatted(namespace);

        String memoryRequestQuery = """
            sum(
              kube_pod_container_resource_requests{namespace="%s", resource="memory"}
            )
            """.formatted(namespace);

        double avgMemoryUsage = prometheusClient.queryValue(memoryUsageQuery);
        double memoryRequest = prometheusClient.queryValue(memoryRequestQuery);

        return calculateResult("MEMORY", avgMemoryUsage, memoryRequest);
    }

    private ResourceAnalysisResult calculateResult(String type, double averageUsage, double request) {

        if (request <= 0) {
            return new ResourceAnalysisResult(
                    type,
                    averageUsage,
                    request,
                    0,
                    "NO_REQUEST",
                    type + " request 값이 설정되어 있지 않습니다."
            );
        }

        double usageRate = (averageUsage / request) * 100.0;

        String status;
        String message;

        if (usageRate < 30) {
            status = "OVER_ALLOCATED";
            message = type + " 사용률이 request 대비 30% 미만입니다. 과할당 가능성이 높습니다.";
        } else if (usageRate > 80) {
            status = "UNDER_REQUESTED";
            message = type + " 사용률이 request 대비 80% 초과입니다. request 부족 가능성이 있습니다.";
        } else {
            status = "NORMAL";
            message = type + " 사용률이 적정 범위입니다.";
        }

        return new ResourceAnalysisResult(
                type,
                averageUsage,
                request,
                Math.round(usageRate * 100.0) / 100.0,
                status,
                message
        );
 
    }
}