# FraudShield Nexus
**Intelligent Digital Payment Fraud Detection & Investigation Platform**

FraudShield Nexus is an integration-ready payment fraud intelligence platform designed to help identify suspicious transaction patterns, assign risk scores, and support human investigation. 

This is a monolithic application featuring a Spring Boot backend, a React/TypeScript frontend, and a PostgreSQL database. It evaluates mobile-money transaction behavior using the synthetic **PaySim** dataset to demonstrate live transaction monitoring and replay.

## Architecture Summary
- **Backend:** Java 17, Spring Boot, Spring Security, Spring Data JPA, Flyway.
- **Frontend:** React, TypeScript, Tailwind CSS, Recharts.
- **Database:** PostgreSQL.
- **Data/Simulation:** Batch ingestion pipeline and live simulated transaction replay engine.

## Folder Structure
```text
.
├── backend/          # Spring Boot application source code
├── data/             # Dataset directory (git ignored)
│   ├── raw/          # Place the raw PaySim CSV here
│   ├── processed/    # Preprocessed/normalized data extracts
│   └── reports/      # Data quality and evaluation reports
├── docs/             # Project documentation, plans, and API contracts
├── frontend/         # React SPA source code
├── ml/               # Python-based ML training and anomaly detection scripts
├── scripts/          # Automation, deployment, and DB setup scripts
└── tests/            # System-wide E2E and integration tests
```

## Setup Prerequisites
1. **Java 17+** and **Maven** (for the backend).
2. **Node.js 18+** (for the frontend).
3. **Docker** and **Docker Compose** (for running PostgreSQL locally).

## Important: Dataset Instructions
This project requires the PaySim mobile-money research dataset to function. 
1. Download the PaySim dataset from Kaggle.
2. Ensure the filename is exactly: `PS_20174392719_1491204439457_log.csv`.
3. Place this file inside the `data/raw/` directory.

*Note: The system will not process transactions until this file is present. Do not commit this file to version control.*
