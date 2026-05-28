package com.example.resourceops.prometheus;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PrometheusClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${prometheus.base-url}")
    private String prometheusBaseUrl;

    public double queryValue(String promQl) {
        RestClient restClient = restClientBuilder
                .baseUrl(prometheusBaseUrl)
                .build();

        Map response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/query")
                        .queryParam("query", promQl)
                        .build())
                .retrieve()
                .body(Map.class);

        if (response == null) {
            return 0.0;
        }

        Map data = (Map) response.get("data");

        if (data == null) {
            return 0.0;
        }

        List result = (List) data.get("result");

        if (result == null || result.isEmpty()) {
            return 0.0;
        }

        Map firstResult = (Map) result.get(0);
        List value = (List) firstResult.get("value");

        if (value == null || value.size() < 2) {
            return 0.0;
        }

        return Double.parseDouble(value.get(1).toString());
    }
}