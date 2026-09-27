package com.fraudshield.evaluation.service;

import com.fraudshield.evaluation.dto.EvaluationMetricsDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EvaluationService {

    private final JdbcTemplate jdbcTemplate;

    public EvaluationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public EvaluationMetricsDTO generateMetrics() {
        String sql = """
            SELECT 
                SUM(CASE WHEN r.category IN ('MEDIUM', 'HIGH') AND t.is_fraud = true THEN 1 ELSE 0 END) as tp,
                SUM(CASE WHEN r.category IN ('MEDIUM', 'HIGH') AND t.is_fraud = false THEN 1 ELSE 0 END) as fp,
                SUM(CASE WHEN r.category = 'LOW' AND t.is_fraud = false THEN 1 ELSE 0 END) as tn,
                SUM(CASE WHEN r.category = 'LOW' AND t.is_fraud = true THEN 1 ELSE 0 END) as fn
            FROM risk_assessments r
            JOIN transactions t ON r.transaction_id = t.id
        """;

        Map<String, Object> result = jdbcTemplate.queryForMap(sql);

        EvaluationMetricsDTO metrics = new EvaluationMetricsDTO();
        
        // Handle nulls in case the tables are completely empty
        metrics.truePositives = getLongValue(result.get("tp"));
        metrics.falsePositives = getLongValue(result.get("fp"));
        metrics.trueNegatives = getLongValue(result.get("tn"));
        metrics.falseNegatives = getLongValue(result.get("fn"));

        metrics.calculateDerivedMetrics();
        return metrics;
    }

    private long getLongValue(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number n) {
            return n.longValue();
        }
        return 0L;
    }
}
