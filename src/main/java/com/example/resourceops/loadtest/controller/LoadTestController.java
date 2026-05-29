package com.example.resourceops.loadtest.controller;

import com.example.resourceops.loadtest.dto.LoadTestResponse;
import com.example.resourceops.loadtest.service.LoadTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@Profile("dev")
@RestController
@RequestMapping("/api/load")
@RequiredArgsConstructor
public class LoadTestController {

    private final LoadTestService loadTestService;

    /**
     * CPU 부하 테스트
     * GET /api/load/cpu?durationMs=500
     * durationMs: 1 ~ 5000ms
     */
    @GetMapping("/cpu")
    public ResponseEntity<LoadTestResponse> cpuLoad(
            @RequestParam(defaultValue = "500") long durationMs
    ) {
        return ResponseEntity.ok(loadTestService.cpuLoad(durationMs));
    }

    /**
     * Memory 부하 테스트
     * GET /api/load/memory?sizeMb=100&holdSeconds=30
     * sizeMb: 1 ~ 128MB
     * holdSeconds: 1 ~ 60s
     */
    @GetMapping("/memory")
    public ResponseEntity<LoadTestResponse> memoryLoad(
            @RequestParam(defaultValue = "100") int sizeMb,
            @RequestParam(defaultValue = "30") int holdSeconds
    ) {
        return ResponseEntity.ok(loadTestService.memoryLoad(sizeMb, holdSeconds));
    }

    /**
     * 응답 지연 테스트
     * GET /api/load/delay?delayMs=1000
     * delayMs: 1 ~ 10000ms
     */
    @GetMapping("/delay")
    public ResponseEntity<LoadTestResponse> delayLoad(
            @RequestParam(defaultValue = "1000") long delayMs
    ) {
        return ResponseEntity.ok(loadTestService.delayLoad(delayMs));
    }

    /**
     * Response size load test
     * GET /api/load/response?sizeKb=5120
     * sizeKb: 1 ~ 10240KB
     */
    @GetMapping(
            value = "/response",
            produces = "application/octet-stream"
    )
    public ResponseEntity<byte[]> responseLoad(
            @RequestParam(defaultValue = "100") int sizeKb
    ) {
        byte[] data = loadTestService.responseLoad(sizeKb);

        return ResponseEntity.ok()
                .header("Content-Length", String.valueOf(data.length))
                .header("Content-Disposition", "attachment; filename=\"load-test-" + sizeKb + "kb.bin\"")
                .body(data);
    }
}
