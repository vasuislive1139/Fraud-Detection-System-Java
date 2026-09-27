package com.fraudshield.api;

import com.fraudshield.core.entity.DatasetImport;
import com.fraudshield.core.repository.DatasetImportRepository;
import com.fraudshield.transaction.DataIngestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final DataIngestionService ingestionService;
    private final DatasetImportRepository importRepository;

    public TransactionController(DataIngestionService ingestionService, DatasetImportRepository importRepository) {
        this.ingestionService = ingestionService;
        this.importRepository = importRepository;
    }

    @PostMapping("/batch")
    public ResponseEntity<?> startBatchImport(@RequestParam String filePath) {
        DatasetImport datasetImport = new DatasetImport(filePath, "PENDING");
        importRepository.save(datasetImport);
        
        // Start async ingestion
        ingestionService.ingestPaySimDataset(datasetImport.getId(), filePath);
        
        return ResponseEntity.accepted().body(Map.of(
            "message", "Import started",
            "importId", datasetImport.getId()
        ));
    }

    @GetMapping("/imports")
    public ResponseEntity<List<DatasetImport>> getImports() {
        return ResponseEntity.ok(importRepository.findAll());
    }
    
    @GetMapping("/imports/{id}")
    public ResponseEntity<DatasetImport> getImportStatus(@PathVariable UUID id) {
        return importRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
