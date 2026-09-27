package com.fraudshield.evaluation.dto;

public class EvaluationMetricsDTO {
    public long truePositives;
    public long falsePositives;
    public long trueNegatives;
    public long falseNegatives;
    
    public double precision;
    public double recall;
    public double f1Score;
    public double falsePositiveRate;
    public double accuracy;

    public void calculateDerivedMetrics() {
        long predictedPositives = truePositives + falsePositives;
        long actualPositives = truePositives + falseNegatives;
        long actualNegatives = falsePositives + trueNegatives;
        long total = predictedPositives + trueNegatives + falseNegatives;

        this.precision = predictedPositives == 0 ? 0 : (double) truePositives / predictedPositives;
        this.recall = actualPositives == 0 ? 0 : (double) truePositives / actualPositives;
        
        if (this.precision + this.recall > 0) {
            this.f1Score = 2 * ((this.precision * this.recall) / (this.precision + this.recall));
        } else {
            this.f1Score = 0;
        }

        this.falsePositiveRate = actualNegatives == 0 ? 0 : (double) falsePositives / actualNegatives;
        this.accuracy = total == 0 ? 0 : (double) (truePositives + trueNegatives) / total;
    }
}
