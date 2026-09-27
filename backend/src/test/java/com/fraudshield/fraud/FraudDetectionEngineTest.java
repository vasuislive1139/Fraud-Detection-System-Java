package com.fraudshield.fraud;

import com.fraudshield.core.entity.FraudRule;
import com.fraudshield.core.entity.RiskAssessment;
import com.fraudshield.core.entity.Transaction;
import com.fraudshield.core.repository.FraudRuleRepository;
import com.fraudshield.core.repository.RiskAssessmentRepository;
import com.fraudshield.core.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FraudDetectionEngineTest {

    @Mock
    private FraudRuleRepository fraudRuleRepository;

    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private com.fraudshield.core.repository.AlertRepository alertRepository;

    @Mock
    private MlServiceClient mlServiceClient;

    @InjectMocks
    private FraudDetectionEngine engine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAssessTransaction_LowRisk() {
        // Arrange
        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID());
        tx.setAmount(new BigDecimal("100.00"));
        tx.setType("PAYMENT");
        tx.setOldBalanceOrig(new BigDecimal("500.00"));
        tx.setEventTime(LocalDateTime.now());

        FraudRule rule = new FraudRule();
        rule.setRuleName("UNUSUAL_AMOUNT");
        rule.setRiskWeight(40);
        rule.setIsActive(true);

        when(fraudRuleRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(rule));
        when(mlServiceClient.predict(any())).thenReturn(new MlServiceClient.MlPrediction(10, java.util.List.of()));
        when(riskAssessmentRepository.save(any(RiskAssessment.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        RiskAssessment assessment = engine.assessTransaction(tx);

        // Assert
        assertNotNull(assessment);
        assertEquals(6, assessment.getTotalScore());
        assertEquals("LOW", assessment.getCategory());
        assertEquals("", assessment.getTriggeredRules());
    }

    @Test
    void testAssessTransaction_HighRisk() {
        // Arrange
        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID());
        tx.setAmount(new BigDecimal("600000.00"));
        tx.setType("TRANSFER");
        tx.setOldBalanceOrig(new BigDecimal("0.00"));
        tx.setNameOrig("C123");
        tx.setEventTime(LocalDateTime.now());

        FraudRule rule1 = new FraudRule();
        rule1.setRuleName("UNUSUAL_AMOUNT");
        rule1.setRiskWeight(40);
        rule1.setIsActive(true);

        FraudRule rule2 = new FraudRule();
        rule2.setRuleName("EMPTY_ACCOUNT_TRANSFER");
        rule2.setRiskWeight(60);
        rule2.setIsActive(true);

        when(fraudRuleRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(rule1, rule2));
        when(mlServiceClient.predict(any())).thenReturn(new MlServiceClient.MlPrediction(90, java.util.List.of("EXTREME_AMOUNT_OUTLIER")));
        when(riskAssessmentRepository.save(any(RiskAssessment.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        RiskAssessment assessment = engine.assessTransaction(tx);

        // Assert
        assertNotNull(assessment);
        assertEquals(94, assessment.getTotalScore()); // 40 + 54
        assertEquals("HIGH", assessment.getCategory());
        assertTrue(assessment.getTriggeredRules().contains("UNUSUAL_AMOUNT"));
        assertTrue(assessment.getTriggeredRules().contains("EMPTY_ACCOUNT_TRANSFER"));
    }
}
