package com.fraudshield.fraud;

import com.fraudshield.core.entity.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class MlServiceClient {

    private final RestTemplate restTemplate;
    
    @Value("${ml.api.url:http://localhost:8000/predict}")
    private String mlApiUrl;

    public MlServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public MlPrediction predict(Transaction tx) {
        try {
            Map<String, Object> request = Map.of(
                "step", tx.getStep(),
                "type", tx.getType(),
                "amount", tx.getAmount(),
                "oldbalanceOrg", tx.getOldBalanceOrig(),
                "newbalanceOrig", tx.getNewBalanceOrig(),
                "oldbalanceDest", tx.getOldBalanceDest(),
                "newbalanceDest", tx.getNewBalanceDest()
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(mlApiUrl, request, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null) {
                int score = (int) body.getOrDefault("ml_score", 0);
                List<String> anomalies = (List<String>) body.getOrDefault("anomalies_detected", List.of());
                return new MlPrediction(score, anomalies);
            }
        } catch (Exception e) {
            // Fallback gracefully if ML service is down
            System.err.println("ML Service unavailable: " + e.getMessage());
        }
        return new MlPrediction(0, List.of());
    }

    public record MlPrediction(int score, List<String> anomalies) {}
}
