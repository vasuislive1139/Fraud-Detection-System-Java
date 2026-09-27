CREATE TABLE fraud_rules (
    id UUID PRIMARY KEY,
    rule_name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    risk_weight INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE risk_assessments (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    total_score INT NOT NULL,
    category VARCHAR(20) NOT NULL, -- LOW, MEDIUM, HIGH
    triggered_rules VARCHAR(1000), -- JSON array of rule names or reason codes
    assessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_risk_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

-- Seed baseline rules
INSERT INTO fraud_rules (id, rule_name, description, risk_weight, is_active) VALUES 
('223e4567-e89b-12d3-a456-426614174001', 'UNUSUAL_AMOUNT', 'Transaction amount exceeds 500,000 baseline', 40, true),
('223e4567-e89b-12d3-a456-426614174002', 'HIGH_VELOCITY', 'More than 3 transactions from the same account in the last hour', 35, true),
('223e4567-e89b-12d3-a456-426614174003', 'EMPTY_ACCOUNT_TRANSFER', 'Attempt to transfer from an account with zero balance', 60, true),
('223e4567-e89b-12d3-a456-426614174004', 'HIGH_RISK_TYPE', 'Large TRANSFER transaction', 20, true);
