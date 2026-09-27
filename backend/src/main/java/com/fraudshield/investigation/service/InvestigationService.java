package com.fraudshield.investigation.service;

import com.fraudshield.core.entity.Alert;
import com.fraudshield.core.entity.CaseNote;
import com.fraudshield.core.entity.InvestigationCase;
import com.fraudshield.core.entity.User;
import com.fraudshield.core.repository.AlertRepository;
import com.fraudshield.core.repository.CaseNoteRepository;
import com.fraudshield.core.repository.InvestigationCaseRepository;
import com.fraudshield.core.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InvestigationService {

    private final AlertRepository alertRepository;
    private final InvestigationCaseRepository caseRepository;
    private final CaseNoteRepository caseNoteRepository;
    private final UserRepository userRepository;

    public InvestigationService(AlertRepository alertRepository, 
                                InvestigationCaseRepository caseRepository, 
                                CaseNoteRepository caseNoteRepository, 
                                UserRepository userRepository) {
        this.alertRepository = alertRepository;
        this.caseRepository = caseRepository;
        this.caseNoteRepository = caseNoteRepository;
        this.userRepository = userRepository;
    }

    public InvestigationCase createCaseFromAlert(UUID alertId, String username) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found"));
        
        if (!"NEW".equals(alert.getStatus())) {
            throw new IllegalStateException("Case can only be created for NEW alerts");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        alert.setStatus("IN_PROGRESS");
        alertRepository.save(alert);

        InvestigationCase invCase = new InvestigationCase();
        invCase.setId(UUID.randomUUID());
        invCase.setAlert(alert);
        invCase.setAssignedUser(user);
        invCase.setStatus("OPEN");
        invCase.setUpdatedAt(LocalDateTime.now());

        return caseRepository.save(invCase);
    }

    public InvestigationCase updateCaseStatus(UUID caseId, String status) {
        InvestigationCase invCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found"));
        
        invCase.setStatus(status);
        invCase.setUpdatedAt(LocalDateTime.now());

        if (status.startsWith("CLOSED")) {
            Alert alert = invCase.getAlert();
            alert.setStatus("CLOSED");
            alertRepository.save(alert);
        }

        return caseRepository.save(invCase);
    }

    public CaseNote addNoteToCase(UUID caseId, String content, String username) {
        InvestigationCase invCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CaseNote note = new CaseNote();
        note.setId(UUID.randomUUID());
        note.setInvestigationCase(invCase);
        note.setAuthor(user);
        note.setContent(content);

        invCase.setUpdatedAt(LocalDateTime.now());
        caseRepository.save(invCase);

        return caseNoteRepository.save(note);
    }

    public List<CaseNote> getNotesForCase(UUID caseId) {
        return caseNoteRepository.findByInvestigationCaseId(caseId);
    }
}
