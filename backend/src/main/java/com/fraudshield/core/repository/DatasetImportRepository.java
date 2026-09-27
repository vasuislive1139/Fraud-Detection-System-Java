package com.fraudshield.core.repository;

import com.fraudshield.core.entity.DatasetImport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DatasetImportRepository extends JpaRepository<DatasetImport, UUID> {
}
