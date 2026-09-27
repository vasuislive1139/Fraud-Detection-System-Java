CREATE TABLE dataset_imports (
    id UUID PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL, -- PENDING, IN_PROGRESS, COMPLETED, FAILED
    rows_processed BIGINT DEFAULT 0,
    total_rows BIGINT DEFAULT 0,
    error_message TEXT,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    step INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    name_orig VARCHAR(100) NOT NULL,
    old_balance_orig DECIMAL(19, 4) NOT NULL,
    new_balance_orig DECIMAL(19, 4) NOT NULL,
    name_dest VARCHAR(100) NOT NULL,
    old_balance_dest DECIMAL(19, 4) NOT NULL,
    new_balance_dest DECIMAL(19, 4) NOT NULL,
    is_fraud BOOLEAN NOT NULL, -- Isolated for evaluation only
    is_flagged_fraud BOOLEAN NOT NULL,
    event_time TIMESTAMP NOT NULL,
    import_id UUID,
    ingested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_import FOREIGN KEY (import_id) REFERENCES dataset_imports(id)
);

CREATE INDEX idx_transactions_step ON transactions(step);
CREATE INDEX idx_transactions_name_orig ON transactions(name_orig);
CREATE INDEX idx_transactions_name_dest ON transactions(name_dest);
