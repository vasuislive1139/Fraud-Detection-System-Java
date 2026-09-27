package com.fraudshield.simulation.service;

import com.fraudshield.core.entity.RiskAssessment;
import com.fraudshield.core.entity.Transaction;
import com.fraudshield.core.repository.TransactionRepository;
import com.fraudshield.fraud.FraudDetectionEngine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SimulationService {

    private final TransactionRepository transactionRepository;
    private final FraudDetectionEngine fraudDetectionEngine;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private ExecutorService executorService;

    public SimulationService(TransactionRepository transactionRepository, FraudDetectionEngine fraudDetectionEngine) {
        this.transactionRepository = transactionRepository;
        this.fraudDetectionEngine = fraudDetectionEngine;
    }

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Infinite timeout
        emitters.add(emitter);
        
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));
        
        return emitter;
    }

    public synchronized void startSimulation(int tps) {
        if (isRunning.get()) {
            throw new IllegalStateException("Simulation is already running");
        }
        
        isRunning.set(true);
        executorService = Executors.newSingleThreadExecutor();
        
        executorService.submit(() -> {
            long delayNanos = tps >= 10000 ? 0 : 1_000_000_000L / tps; // No delay if TPS is maxed
            
            java.io.File csvFile = new java.io.File("data/raw/PS_20174392719_1491204439457_log.csv");
            java.io.File checkpointFile = new java.io.File("data/raw/checkpoint.txt");
            
            long startLine = 0;
            if (checkpointFile.exists()) {
                try {
                    startLine = Long.parseLong(new String(java.nio.file.Files.readAllBytes(checkpointFile.toPath())).trim());
                } catch (Exception e) {}
            }
            
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(csvFile))) {
                String line;
                long currentLine = 0;
                
                while (isRunning.get() && (line = br.readLine()) != null) {
                    if (currentLine <= startLine && currentLine > 0) {
                        currentLine++;
                        continue; // Fast-forward to checkpoint
                    }
                    currentLine++;
                    
                    long startTime = System.nanoTime();
                    
                    String[] data = line.split(",");
                    if (data.length < 9) continue;
                    
                    Transaction tx = new Transaction();
                    tx.setId(java.util.UUID.randomUUID());
                    try {
                        tx.setStep(Integer.parseInt(data[0]));
                        tx.setType(data[1]);
                        tx.setAmount(new java.math.BigDecimal(data[2]));
                        tx.setNameOrig(data[3]);
                        tx.setOldBalanceOrig(new java.math.BigDecimal(data[4]));
                        tx.setNewBalanceOrig(new java.math.BigDecimal(data[5]));
                        tx.setNameDest(data[6]);
                        tx.setOldBalanceDest(new java.math.BigDecimal(data[7]));
                        tx.setNewBalanceDest(new java.math.BigDecimal(data[8]));
                        tx.setEventTime(java.time.LocalDateTime.now());
                    } catch (Exception e) {
                        continue; // Skip malformed rows
                    }

                    if (!isRunning.get()) break;

                    RiskAssessment assessment = fraudDetectionEngine.assessTransaction(tx);
                    
                    // Attach the action based on risk level so the frontend can route it
                    if ("HIGH".equals(assessment.getCategory())) {
                        assessment.setTriggeredRules(assessment.getTriggeredRules() + ",ACTION:BLOCKED_HIGH_PRIORITY");
                    } else if ("MEDIUM".equals(assessment.getCategory())) {
                        assessment.setTriggeredRules(assessment.getTriggeredRules() + ",ACTION:MANUAL_REVIEW");
                    } else {
                        assessment.setTriggeredRules(assessment.getTriggeredRules() + ",ACTION:AUTO_APPROVED");
                    }

                    broadcast(assessment);

                    if (currentLine % 1000 == 0) {
                        try {
                            java.nio.file.Files.write(checkpointFile.toPath(), String.valueOf(currentLine).getBytes());
                        } catch (Exception e) {}
                    }

                    if (delayNanos > 0) {
                        long elapsedNanos = System.nanoTime() - startTime;
                        long sleepNanos = delayNanos - elapsedNanos;
                        if (sleepNanos > 0) {
                            Thread.sleep(sleepNanos / 1_000_000, (int) (sleepNanos % 1_000_000));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reading CSV: " + e.getMessage());
            } finally {
                isRunning.set(false);
            }
        });
    }

    public void stopSimulation() {
        isRunning.set(false);
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    private void broadcast(RiskAssessment assessment) {
        com.fraudshield.simulation.dto.RiskAssessmentEventDTO dto = com.fraudshield.simulation.dto.RiskAssessmentEventDTO.from(assessment);
        List<SseEmitter> deadEmitters = new java.util.ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("transaction")
                        .data(dto));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }
}
