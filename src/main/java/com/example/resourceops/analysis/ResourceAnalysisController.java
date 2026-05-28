package com.example.resourceops.analysis;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class ResourceAnalysisController {

    private final ResourceAnalysisService resourceAnalysisService;

    @GetMapping("/cpu")
    public ResourceAnalysisResult analyzeCpu(@RequestParam String namespace) {
        return resourceAnalysisService.analyzeCpu(namespace);
    }

    @GetMapping("/memory")
    public ResourceAnalysisResult analyzeMemory(@RequestParam String namespace) {
        return resourceAnalysisService.analyzeMemory(namespace);
    }
}