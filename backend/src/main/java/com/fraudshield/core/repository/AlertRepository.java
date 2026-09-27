package com.fraudshield.core.repository;

import com.fraudshield.core.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
}
