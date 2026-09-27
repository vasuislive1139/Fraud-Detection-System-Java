package com.fraudshield.transaction;

import com.fraudshield.core.entity.DatasetImport;
import com.fraudshield.core.entity.Transaction;
import com.fraudshield.core.repository.DatasetImportRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DataIngestionService {

    private final TransactionJdbcRepository jdbcRepository;
    private final DatasetImportRepository importRepository;

    public DataIngestionService(TransactionJdbcRepository jdbcRepository, DatasetImportRepository importRepository) {
        this.jdbcRepository = jdbcRepository;
        this.importRepository = importRepository;
    }

    @Async
    public void ingestPaySimDataset(UUID importId, String filePath) {
        DatasetImport datasetImport = importRepository.findById(importId)
                .orElseThrow(() -> new IllegalArgumentException("Import not found"));

        datasetImport.setStatus("IN_PROGRESS");
        importRepository.save(datasetImport);

        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 0, 0);
        long processedCount = 0;
        int batchSize = 10000;
        List<Transaction> batch = new ArrayList<>(batchSize);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }

                String[] cols = line.split(",");
                if (cols.length < 11) continue;

                Transaction tx = new Transaction();
                tx.setId(UUID.randomUUID());
                tx.setStep(Integer.parseInt(cols[0]));
                tx.setType(cols[1]);
                tx.setAmount(new BigDecimal(cols[2]));
                tx.setNameOrig(cols[3]);
                tx.setOldBalanceOrig(new BigDecimal(cols[4]));
                tx.setNewBalanceOrig(new BigDecimal(cols[5]));
                tx.setNameDest(cols[6]);
                tx.setOldBalanceDest(new BigDecimal(cols[7]));
                tx.setNewBalanceDest(new BigDecimal(cols[8]));
                tx.setIsFraud(cols[9].equals("1"));
                tx.setIsFlaggedFraud(cols[10].equals("1"));
                
                tx.setEventTime(baseTime.plusHours(tx.getStep()));
                tx.setDatasetImport(datasetImport);

                batch.add(tx);
                processedCount++;

                if (batch.size() >= batchSize) {
                    jdbcRepository.batchInsert(batch);
                    batch.clear();
                    
                    datasetImport.setRowsProcessed(processedCount);
                    importRepository.save(datasetImport);
                }
            }

            // Insert remaining
            if (!batch.isEmpty()) {
                jdbcRepository.batchInsert(batch);
                datasetImport.setRowsProcessed(processedCount);
            }

            datasetImport.setStatus("COMPLETED");
            datasetImport.setTotalRows(processedCount);
            datasetImport.setCompletedAt(LocalDateTime.now());
            importRepository.save(datasetImport);

        } catch (Exception e) {
            datasetImport.setStatus("FAILED");
            datasetImport.setErrorMessage(e.getMessage());
            datasetImport.setCompletedAt(LocalDateTime.now());
            importRepository.save(datasetImport);
        }
    }
}
