package com.fraudshield.core.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dataset_imports")
public class DatasetImport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String status;

    @Column(name = "rows_processed")
    private Long rowsProcessed = 0L;

    @Column(name = "total_rows")
    private Long totalRows = 0L;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "started_at", insertable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public DatasetImport() {
    }

    public DatasetImport(String fileName, String status) {
        this.fileName = fileName;
        this.status = status;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getRowsProcessed() { return rowsProcessed; }
    public void setRowsProcessed(Long rowsProcessed) { this.rowsProcessed = rowsProcessed; }
    public Long getTotalRows() { return totalRows; }
    public void setTotalRows(Long totalRows) { this.totalRows = totalRows; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
