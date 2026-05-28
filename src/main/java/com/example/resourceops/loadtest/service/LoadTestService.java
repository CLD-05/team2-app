package com.example.resourceops.loadtest.service;

import com.example.resourceops.loadtest.dto.LoadTestResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoadTestService {

    private static final long MAX_DURATION_MS = 5000;
    private static final int MAX_MEMORY_MB = 128;
    private static final int MAX_HOLD_SECONDS = 60;
    private static final long MAX_DELAY_MS = 10000;

    public LoadTestResponse cpuLoad(long durationMs) {
        if (durationMs < 1 || durationMs > MAX_DURATION_MS) {
            throw new IllegalArgumentException("durationMs must be between 1 and " + MAX_DURATION_MS);
        }

        long endTime = System.currentTimeMillis() + durationMs;
        double value = 0;
        while (System.currentTimeMillis() < endTime) {
            value += Math.sqrt(Math.random());
        }

        return new LoadTestResponse("CPU", durationMs, "CPU load generated for " + durationMs + "ms");
    }

    public LoadTestResponse memoryLoad(int sizeMb, int holdSeconds) {
        if (sizeMb < 1 || sizeMb > MAX_MEMORY_MB) {
            throw new IllegalArgumentException("sizeMb must be between 1 and " + MAX_MEMORY_MB);
        }
        if (holdSeconds < 1 || holdSeconds > MAX_HOLD_SECONDS) {
            throw new IllegalArgumentException("holdSeconds must be between 1 and " + MAX_HOLD_SECONDS);
        }

        List<byte[]> allocated = new ArrayList<>();
        try {
            allocated.add(new byte[sizeMb * 1024 * 1024]);
            Thread.sleep(holdSeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            allocated.clear();
        }

        return new LoadTestResponse("MEMORY", sizeMb + "MB / " + holdSeconds + "s",
                "Memory load generated: " + sizeMb + "MB held for " + holdSeconds + "s");
    }

    public LoadTestResponse delayLoad(long delayMs) {
        if (delayMs < 1 || delayMs > MAX_DELAY_MS) {
            throw new IllegalArgumentException("delayMs must be between 1 and " + MAX_DELAY_MS);
        }

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return new LoadTestResponse("DELAY", delayMs, "Delay of " + delayMs + "ms applied");
    }
}