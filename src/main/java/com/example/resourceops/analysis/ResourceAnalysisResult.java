package com.example.resourceops.analysis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResourceAnalysisResult {

    private String type;          // CPU or MEMORY
    private double averageUsage;  // 평균 사용량
    private double request;       // 설정된 request 값
    private double usageRate;     // request 대비 사용률 %
    private String status;        // OVER_ALLOCATED / NORMAL / UNDER_REQUESTED
    private String message;
}