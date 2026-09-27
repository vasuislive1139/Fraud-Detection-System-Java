package com.fraudshield.core.repository;

import com.fraudshield.core.entity.InvestigationCase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface InvestigationCaseRepository extends JpaRepository<InvestigationCase, UUID> {
}
