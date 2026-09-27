package com.fraudshield.simulation.dto;

import com.fraudshield.core.entity.RiskAssessment;
import com.fraudshield.core.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class RiskAssessmentEventDTO {
    public UUID id;
    public Integer totalScore;
    public String category;
    public String triggeredRules;
    public LocalDateTime assessedAt;
    
    // Transaction details
    public UUID txId;
    public String type;
    public BigDecimal amount;
    public String nameOrig;
    public String nameDest;
    public LocalDateTime eventTime;

    public static RiskAssessmentEventDTO from(RiskAssessment assessment) {
        RiskAssessmentEventDTO dto = new RiskAssessmentEventDTO();
        dto.id = assessment.getId();
        dto.totalScore = assessment.getTotalScore();
        dto.category = assessment.getCategory();
        dto.triggeredRules = assessment.getTriggeredRules();
        dto.assessedAt = assessment.getAssessedAt();

        Transaction tx = assessment.getTransaction();
        if (tx != null) {
            dto.txId = tx.getId();
            dto.type = tx.getType();
            dto.amount = tx.getAmount();
            dto.nameOrig = tx.getNameOrig();
            dto.nameDest = tx.getNameDest();
            dto.eventTime = tx.getEventTime();
        }
        return dto;
    }
}
