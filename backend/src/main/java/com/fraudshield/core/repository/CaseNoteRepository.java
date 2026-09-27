package com.fraudshield.core.repository;

import com.fraudshield.core.entity.CaseNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface CaseNoteRepository extends JpaRepository<CaseNote, UUID> {
    List<CaseNote> findByInvestigationCaseId(UUID caseId);
}
