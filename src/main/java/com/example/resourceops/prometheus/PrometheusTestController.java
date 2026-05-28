package com.example.resourceops.prometheus;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/api/prometheus")

@RequiredArgsConstructor

public class PrometheusTestController {

    private final PrometheusClient prometheusClient;

    @GetMapping("/query")

    public double query(@RequestParam String promql) {

        return prometheusClient.queryValue(promql);

    }

}