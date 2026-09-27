# Implementation Plan: FraudShield Nexus

Based on the initial proposal, this project follows a strict milestone-driven development process.

## M0 — Discovery (Completed)
- Dataset inspected (pending manual placement).
- Requirements and assumptions documented.
- Architecture and database design approved.

## M1 — Foundation (In Progress / Partially Complete)
- Spring Boot monolithic app initialization.
- PostgreSQL database setup via `docker-compose`.
- Initial Flyway migrations.
- Authentication/RBAC baseline.
- Unit and integration test setup.

## M2 — Data Ingestion
- Analyze PaySim CSV dataset properties (schema, imbalance).
- Implement performant CSV parsing (batching to avoid OOM).
- Data validation and quality reporting.
- Load historical transactions into the database securely without data leakage.

## M3 — Baseline Engine
- Build the core rule evaluation engine.
- Configure risk assessment algorithms (amount, velocity, unusual chains).
- Implement explainability (reason codes).
- Write comprehensive unit tests for rules.

## M4 — Alerts and Cases
- Define the alert lifecycle.
- Implement the case workflow (assignment, status changes).
- Add support for analyst notes and audit logging.

## M5 — Frontend
- Scaffold React + TypeScript + Tailwind SPA.
- Build Dashboard (total transactions, risk distribution charts).
- Transaction search and detail pages.
- Alert queue and case investigation UI.
- Integrate frontend with real backend REST APIs.

## M6 — Replay and Simulation
- Implement Replay controls (start, pause, resume streaming transactions).
- Establish WebSocket or SSE for live dashboard updates.
- Create a simulated payment authorization gateway.

## M7 — Evaluation
- Apply a leakage-safe split (Train / Val / Test).
- Generate baseline evaluation metrics (Precision, Recall, False-Positive Rate).
- Output reproducible metric reports.

## M8 — Advanced Options
- Explore Machine Learning integration.
- Train supervised classification and anomaly detection models.
- Build account relationship graphs.

## M9 — Delivery
- Final security and performance tuning.
- Polish project documentation and deployment instructions.
- Prepare demo scripts and setup guides.
