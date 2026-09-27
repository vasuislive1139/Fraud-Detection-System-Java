# Dataset Analysis Plan

This document outlines the systematic checklist that will be executed **only after** the PaySim CSV dataset (`PS_20174392719_1491204439457_log.csv`) has been manually placed in the `data/raw/` directory.

## 1. Schema & Data Quality Inspection
- [ ] Parse headers to confirm expected columns (`step`, `type`, `amount`, `nameOrig`, `oldbalanceOrg`, `newbalanceOrig`, `nameDest`, `oldbalanceDest`, `newbalanceDest`, `isFraud`, `isFlaggedFraud`).
- [ ] Determine the total row count (expected ~6.36 million records).
- [ ] Check for missing values (null/NaN) across all columns.
- [ ] Verify data types (e.g., numeric balances, string identifiers).
- [ ] Check for duplicate transaction records.

## 2. Statistical & Class Imbalance Review
- [ ] Count total legitimate vs. fraudulent transactions (`isFraud` = 0 vs 1).
- [ ] Calculate the exact class imbalance ratio.
- [ ] Analyze the distribution of transaction `type` (e.g., TRANSFER, CASH_OUT, PAYMENT, DEBIT, CASH_IN) relative to fraud instances.

## 3. Data Leakage Assessment
- [ ] Ensure that `isFraud` and `isFlaggedFraud` are strictly held out from any predictive modeling or scoring logic during transaction ingestion.
- [ ] Identify if any other feature (like `newbalanceDest`) could subtly leak future knowledge at the exact moment of the transaction.

## 4. Ingestion Strategy & Preprocessing
- [ ] Determine batch size for safe database ingestion (e.g., 50k - 100k rows per batch) to prevent memory exhaustion (OOM).
- [ ] Formulate a normalization plan: should `step` (which represents 1 hour of time) be converted to a more realistic datetime timestamp relative to a base epoch?
- [ ] Define the Train/Validation/Test split strategy (e.g., time-based split using the `step` variable).
