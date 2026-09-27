package com.fraudshield.api;

import com.fraudshield.core.entity.FraudRule;
import com.fraudshield.core.entity.RiskAssessment;
import com.fraudshield.core.entity.Transaction;
import com.fraudshield.core.repository.FraudRuleRepository;
import com.fraudshield.core.repository.TransactionRepository;
import com.fraudshield.fraud.FraudDetectionEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fraud")
public class FraudController {

    private final FraudDetectionEngine engine;
    private final TransactionRepository transactionRepository;
    private final FraudRuleRepository fraudRuleRepository;

    public FraudController(FraudDetectionEngine engine, 
                           TransactionRepository transactionRepository,
                           FraudRuleRepository fraudRuleRepository) {
        this.engine = engine;
        this.transactionRepository = transactionRepository;
        this.fraudRuleRepository = fraudRuleRepository;
    }

    @PostMapping("/score/{transactionId}")
    public ResponseEntity<RiskAssessment> scoreTransaction(@PathVariable UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .map(tx -> ResponseEntity.ok(engine.assessTransaction(tx)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rules")
    public ResponseEntity<List<FraudRule>> getRules() {
        return ResponseEntity.ok(fraudRuleRepository.findAll());
    }
}
