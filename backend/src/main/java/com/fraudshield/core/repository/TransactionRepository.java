package com.fraudshield.core.repository;

import com.fraudshield.core.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    long countByNameOrigAndEventTimeBetween(String nameOrig, LocalDateTime startTime, LocalDateTime endTime);
}
