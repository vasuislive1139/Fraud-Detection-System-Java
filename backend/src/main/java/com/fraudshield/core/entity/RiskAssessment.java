package com.fraudshield.core.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessment {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    private Integer totalScore;
    private String category;
    
    @Column(name = "triggered_rules")
    private String triggeredRules; // Comma separated list of rule names or JSON array

    @Column(name = "assessed_at", insertable = false, updatable = false)
    private LocalDateTime assessedAt;

    public RiskAssessment() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTriggeredRules() { return triggeredRules; }
    public void setTriggeredRules(String triggeredRules) { this.triggeredRules = triggeredRules; }

    public LocalDateTime getAssessedAt() { return assessedAt; }
    public void setAssessedAt(LocalDateTime assessedAt) { this.assessedAt = assessedAt; }
}
