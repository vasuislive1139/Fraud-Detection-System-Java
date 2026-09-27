package com.fraudshield.core.repository;

import com.fraudshield.core.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, UUID> {
}
