package com.fraudshield.transaction;

import com.fraudshield.core.entity.DatasetImport;
import com.fraudshield.core.repository.DatasetImportRepository;
import com.fraudshield.core.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class DataIngestionServiceTest {

    @Autowired
    private DataIngestionService ingestionService;

    @Autowired
    private DatasetImportRepository importRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void testIngestSampleDataset() throws Exception {
        DatasetImport datasetImport = new DatasetImport("sample_paysim.csv", "PENDING");
        datasetImport = importRepository.save(datasetImport);

        // Run synchronously for test by calling the method directly, though it has @Async it might run in a different thread.
        // In a SpringBootTest, if async is enabled, it returns immediately. So we wait.
        ingestionService.ingestPaySimDataset(datasetImport.getId(), "src/test/resources/data/sample_paysim.csv");
        
        // Wait up to 5 seconds for it to finish
        int attempts = 0;
        DatasetImport updatedImport = importRepository.findById(datasetImport.getId()).orElseThrow();
        while (!"COMPLETED".equals(updatedImport.getStatus()) && !"FAILED".equals(updatedImport.getStatus()) && attempts < 50) {
            Thread.sleep(100);
            updatedImport = importRepository.findById(datasetImport.getId()).orElseThrow();
            attempts++;
        }

        assertEquals("COMPLETED", updatedImport.getStatus(), "Status should be COMPLETED. Error: " + updatedImport.getErrorMessage());
        assertEquals(4, updatedImport.getTotalRows());

        long txCount = transactionRepository.count();
        assertEquals(4, txCount, "Should have inserted 4 transactions");
    }
}
