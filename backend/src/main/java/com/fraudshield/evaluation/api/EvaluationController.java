package com.fraudshield.evaluation.api;

import com.fraudshield.evaluation.dto.EvaluationMetricsDTO;
import com.fraudshield.evaluation.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/evaluation")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<EvaluationMetricsDTO> getMetrics() {
        return ResponseEntity.ok(evaluationService.generateMetrics());
    }
}
