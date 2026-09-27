package com.fraudshield.evaluation.service;

import com.fraudshield.evaluation.dto.EvaluationMetricsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class EvaluationServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private EvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateMetrics() {
        // Arrange
        Map<String, Object> mockResult = Map.of(
            "tp", 100L,
            "fp", 50L,
            "tn", 900L,
            "fn", 10L
        );
        when(jdbcTemplate.queryForMap(anyString())).thenReturn(mockResult);

        // Act
        EvaluationMetricsDTO metrics = evaluationService.generateMetrics();

        // Assert
        assertEquals(100L, metrics.truePositives);
        assertEquals(50L, metrics.falsePositives);
        assertEquals(900L, metrics.trueNegatives);
        assertEquals(10L, metrics.falseNegatives);

        // 100 / 150 = 0.666...
        assertEquals(0.666, metrics.precision, 0.001);
        // 100 / 110 = 0.909...
        assertEquals(0.909, metrics.recall, 0.001);
        // (100 + 900) / 1060 = 1000 / 1060 = 0.943...
        assertEquals(0.943, metrics.accuracy, 0.001);
    }
}
