CREATE TABLE alerts (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    risk_assessment_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL, -- NEW, IN_PROGRESS, CLOSED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alert_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id),
    CONSTRAINT fk_alert_assessment FOREIGN KEY (risk_assessment_id) REFERENCES risk_assessments(id)
);

CREATE TABLE cases (
    id UUID PRIMARY KEY,
    alert_id UUID NOT NULL,
    assigned_user_id UUID,
    status VARCHAR(50) NOT NULL, -- OPEN, CLOSED_TRUE_POSITIVE, CLOSED_FALSE_POSITIVE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_case_alert FOREIGN KEY (alert_id) REFERENCES alerts(id),
    CONSTRAINT fk_case_user FOREIGN KEY (assigned_user_id) REFERENCES users(id)
);

CREATE TABLE case_notes (
    id UUID PRIMARY KEY,
    case_id UUID NOT NULL,
    author_id UUID NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_casenote_case FOREIGN KEY (case_id) REFERENCES cases(id),
    CONSTRAINT fk_casenote_user FOREIGN KEY (author_id) REFERENCES users(id)
);
