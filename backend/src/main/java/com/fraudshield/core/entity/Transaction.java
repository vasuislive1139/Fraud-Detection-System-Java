package com.fraudshield.core.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    private UUID id;

    private Integer step;
    private String type;
    private BigDecimal amount;
    
    @Column(name = "name_orig")
    private String nameOrig;
    
    @Column(name = "old_balance_orig")
    private BigDecimal oldBalanceOrig;
    
    @Column(name = "new_balance_orig")
    private BigDecimal newBalanceOrig;
    
    @Column(name = "name_dest")
    private String nameDest;
    
    @Column(name = "old_balance_dest")
    private BigDecimal oldBalanceDest;
    
    @Column(name = "new_balance_dest")
    private BigDecimal newBalanceDest;
    
    @Column(name = "is_fraud")
    private Boolean isFraud;
    
    @Column(name = "is_flagged_fraud")
    private Boolean isFlaggedFraud;
    
    @Column(name = "event_time")
    private LocalDateTime eventTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_id")
    private DatasetImport datasetImport;

    @Column(name = "ingested_at", insertable = false, updatable = false)
    private LocalDateTime ingestedAt;

    public Transaction() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Integer getStep() { return step; }
    public void setStep(Integer step) { this.step = step; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getNameOrig() { return nameOrig; }
    public void setNameOrig(String nameOrig) { this.nameOrig = nameOrig; }
    public BigDecimal getOldBalanceOrig() { return oldBalanceOrig; }
    public void setOldBalanceOrig(BigDecimal oldBalanceOrig) { this.oldBalanceOrig = oldBalanceOrig; }
    public BigDecimal getNewBalanceOrig() { return newBalanceOrig; }
    public void setNewBalanceOrig(BigDecimal newBalanceOrig) { this.newBalanceOrig = newBalanceOrig; }
    public String getNameDest() { return nameDest; }
    public void setNameDest(String nameDest) { this.nameDest = nameDest; }
    public BigDecimal getOldBalanceDest() { return oldBalanceDest; }
    public void setOldBalanceDest(BigDecimal oldBalanceDest) { this.oldBalanceDest = oldBalanceDest; }
    public BigDecimal getNewBalanceDest() { return newBalanceDest; }
    public void setNewBalanceDest(BigDecimal newBalanceDest) { this.newBalanceDest = newBalanceDest; }
    public Boolean getIsFraud() { return isFraud; }
    public void setIsFraud(Boolean isFraud) { this.isFraud = isFraud; }
    public Boolean getIsFlaggedFraud() { return isFlaggedFraud; }
    public void setIsFlaggedFraud(Boolean isFlaggedFraud) { this.isFlaggedFraud = isFlaggedFraud; }
    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
    public DatasetImport getDatasetImport() { return datasetImport; }
    public void setDatasetImport(DatasetImport datasetImport) { this.datasetImport = datasetImport; }
    public LocalDateTime getIngestedAt() { return ingestedAt; }
    public void setIngestedAt(LocalDateTime ingestedAt) { this.ingestedAt = ingestedAt; }
}
