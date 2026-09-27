package com.fraudshield.investigation.api;

import com.fraudshield.core.entity.Alert;
import com.fraudshield.core.entity.CaseNote;
import com.fraudshield.core.entity.InvestigationCase;
import com.fraudshield.core.repository.AlertRepository;
import com.fraudshield.core.repository.InvestigationCaseRepository;
import com.fraudshield.investigation.service.InvestigationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class InvestigationController {

    private final InvestigationService investigationService;
    private final AlertRepository alertRepository;
    private final InvestigationCaseRepository caseRepository;

    public InvestigationController(InvestigationService investigationService, 
                                   AlertRepository alertRepository, 
                                   InvestigationCaseRepository caseRepository) {
        this.investigationService = investigationService;
        this.alertRepository = alertRepository;
        this.caseRepository = caseRepository;
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getAlerts() {
        return ResponseEntity.ok(alertRepository.findAll());
    }

    @PostMapping("/cases")
    public ResponseEntity<InvestigationCase> createCase(@RequestParam UUID alertId, Authentication auth) {
        return ResponseEntity.ok(investigationService.createCaseFromAlert(alertId, auth.getName()));
    }

    @GetMapping("/cases")
    public ResponseEntity<List<InvestigationCase>> getCases() {
        return ResponseEntity.ok(caseRepository.findAll());
    }

    @PatchMapping("/cases/{id}")
    public ResponseEntity<InvestigationCase> updateCaseStatus(@PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(investigationService.updateCaseStatus(id, status));
    }

    @PostMapping("/cases/{id}/notes")
    public ResponseEntity<CaseNote> addNote(@PathVariable UUID id, @RequestBody String content, Authentication auth) {
        return ResponseEntity.ok(investigationService.addNoteToCase(id, content, auth.getName()));
    }

    @GetMapping("/cases/{id}/notes")
    public ResponseEntity<List<CaseNote>> getNotes(@PathVariable UUID id) {
        return ResponseEntity.ok(investigationService.getNotesForCase(id));
    }
}
