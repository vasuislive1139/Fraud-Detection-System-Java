package com.fraudshield.core.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_rules")
public class FraudRule {

    @Id
    private UUID id;

    private String ruleName;
    private String description;
    private Integer riskWeight;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public FraudRule() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getRiskWeight() { return riskWeight; }
    public void setRiskWeight(Integer riskWeight) { this.riskWeight = riskWeight; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
