package com.fraudshield.investigation.service;

import com.fraudshield.core.entity.Alert;
import com.fraudshield.core.entity.InvestigationCase;
import com.fraudshield.core.entity.User;
import com.fraudshield.core.repository.AlertRepository;
import com.fraudshield.core.repository.CaseNoteRepository;
import com.fraudshield.core.repository.InvestigationCaseRepository;
import com.fraudshield.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InvestigationServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private InvestigationCaseRepository caseRepository;

    @Mock
    private CaseNoteRepository caseNoteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InvestigationService investigationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCaseFromAlert() {
        UUID alertId = UUID.randomUUID();
        Alert alert = new Alert();
        alert.setId(alertId);
        alert.setStatus("NEW");

        User user = new User();
        user.setUsername("analyst1");

        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(userRepository.findByUsername("analyst1")).thenReturn(Optional.of(user));
        when(caseRepository.save(any(InvestigationCase.class))).thenAnswer(i -> i.getArguments()[0]);

        InvestigationCase result = investigationService.createCaseFromAlert(alertId, "analyst1");

        assertNotNull(result);
        assertEquals("OPEN", result.getStatus());
        assertEquals("IN_PROGRESS", alert.getStatus());
        verify(alertRepository).save(alert);
        verify(caseRepository).save(any(InvestigationCase.class));
    }
}
