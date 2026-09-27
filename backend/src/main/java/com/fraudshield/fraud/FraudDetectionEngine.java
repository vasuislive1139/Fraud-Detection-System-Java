package com.fraudshield.fraud;

import com.fraudshield.core.entity.FraudRule;
import com.fraudshield.core.entity.RiskAssessment;
import com.fraudshield.core.entity.Transaction;
import com.fraudshield.core.repository.FraudRuleRepository;
import com.fraudshield.core.repository.RiskAssessmentRepository;
import com.fraudshield.core.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fraudshield.core.entity.Alert;
import com.fraudshield.core.repository.AlertRepository;

@Service
public class FraudDetectionEngine {

    private final FraudRuleRepository fraudRuleRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final TransactionRepository transactionRepository;
    private final AlertRepository alertRepository;
    private final MlServiceClient mlServiceClient;

    public FraudDetectionEngine(FraudRuleRepository fraudRuleRepository, 
                                RiskAssessmentRepository riskAssessmentRepository,
                                TransactionRepository transactionRepository,
                                AlertRepository alertRepository,
                                MlServiceClient mlServiceClient) {
        this.fraudRuleRepository = fraudRuleRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.transactionRepository = transactionRepository;
        this.alertRepository = alertRepository;
        this.mlServiceClient = mlServiceClient;
    }

    public RiskAssessment assessTransaction(Transaction tx) {
        List<FraudRule> activeRules = fraudRuleRepository.findByIsActiveTrue();
        int totalScore = 0;
        List<String> triggeredRuleNames = new ArrayList<>();

        for (FraudRule rule : activeRules) {
            boolean triggered = evaluateRule(rule, tx);
            if (triggered) {
                totalScore += rule.getRiskWeight();
                triggeredRuleNames.add(rule.getRuleName());
            }
        }

        // Call Python ML Microservice
        MlServiceClient.MlPrediction mlPrediction = mlServiceClient.predict(tx);
        
        // Blend Scores (E.g., 60% weight to ML, 40% to Rules)
        int blendedScore = (int) ((totalScore * 0.4) + (mlPrediction.score() * 0.6));
        
        // Add ML anomalies to triggered rules string
        for (String anomaly : mlPrediction.anomalies()) {
            triggeredRuleNames.add("ML_ANOMALY:" + anomaly);
        }

        // Cap score at 100
        if (blendedScore > 100) blendedScore = 100;

        String category = determineCategory(blendedScore);

        RiskAssessment assessment = new RiskAssessment();
        assessment.setId(UUID.randomUUID());
        assessment.setTransaction(tx);
        assessment.setTotalScore(blendedScore);
        assessment.setCategory(category);
        assessment.setTriggeredRules(String.join(",", triggeredRuleNames));

        RiskAssessment savedAssessment = riskAssessmentRepository.save(assessment);

        if ("MEDIUM".equals(category) || "HIGH".equals(category)) {
            Alert alert = new Alert();
            alert.setId(UUID.randomUUID());
            alert.setTransaction(tx);
            alert.setRiskAssessment(savedAssessment);
            alert.setStatus("NEW");
            alertRepository.save(alert);
        }

        return savedAssessment;
    }

    private boolean evaluateRule(FraudRule rule, Transaction tx) {
        return switch (rule.getRuleName()) {
            case "UNUSUAL_AMOUNT" -> tx.getAmount().compareTo(new BigDecimal("500000")) > 0;
            case "HIGH_VELOCITY" -> {
                LocalDateTime oneHourAgo = tx.getEventTime().minusHours(1);
                long count = transactionRepository.countByNameOrigAndEventTimeBetween(
                        tx.getNameOrig(), oneHourAgo, tx.getEventTime());
                yield count >= 3;
            }
            case "EMPTY_ACCOUNT_TRANSFER" -> tx.getOldBalanceOrig().compareTo(BigDecimal.ZERO) == 0 
                                            && tx.getAmount().compareTo(BigDecimal.ZERO) > 0;
            case "HIGH_RISK_TYPE" -> "TRANSFER".equalsIgnoreCase(tx.getType()) 
                                    && tx.getAmount().compareTo(new BigDecimal("200000")) > 0;
            default -> false; // Unknown rules do not trigger
        };
    }

    private String determineCategory(int score) {
        if (score < 30) return "LOW";
        if (score < 70) return "MEDIUM";
        return "HIGH";
    }
}
