# Wallet — Event-Driven Payment Backend

A backend system that models the core architecture of a real payment/fintech platform: a double-entry ledger, concurrency-safe money transfers, JWT-based security, an event-driven microservice architecture over Kafka, and ML-based fraud detection built as a separate Python service.

> This is a backend engineering portfolio project. There is no frontend by design — the API is explored through Swagger UI, the way a real backend service is consumed.

---

## What it does

- **Accounts & balances** — balances are never stored directly; they are always derived from the transaction ledger, so the ledger is the single source of truth.
- **Money transfers** — atomic, concurrency-safe transfers between accounts using double-entry accounting (every movement is recorded as balanced debit/credit entries).
- **Authentication & authorization** — user registration with BCrypt-hashed passwords, JWT-based stateless authentication, and ownership-based access control (a user can only access and transfer from their own accounts).
- **Event-driven side effects** — every transfer publishes an event to Kafka; independent consumers handle audit logging and fraud detection without slowing down or coupling to the core transfer path.
- **Fraud detection** — a separate Python microservice consumes transfer events, scores them with a trained machine-learning model (Isolation Forest), and publishes results back over Kafka; the main service flags suspicious transfers in the database.

---

## Architecture

```
                    ┌──────────────────────┐
                    │   Wallet Service      │
                    │   (Java / Spring)     │
                    │                       │
   HTTP ──────────► │  - accounts / ledger  │
                    │  - transfers          │
                    │  - JWT auth           │
                    └───────┬───────────────┘
                            │ publishes
                            ▼
                    ┌───────────────┐        ┌──────────────────────┐
                    │   Kafka       │        │  Fraud Service        │
                    │  "transfers"  │ ─────► │  (Python / FastAPI)   │
                    │               │        │  - ML scoring         │
                    │ "fraud-results"│ ◄──── │    (Isolation Forest) │
                    └───────┬───────┘        └──────────────────────┘
                            │ consumes
                            ▼
                    Wallet Service flags
                    suspicious transfers
```

The core money movement (ledger writes) stays **synchronous and atomic** — it must be all-or-nothing consistent. Only independent side effects (audit, fraud) are made **asynchronous** through events, so a slow or failed side effect never affects the correctness of a transfer.

---

## Tech stack

**Wallet service (core)**
- Java 21, Spring Boot
- Spring Security + JWT (jjwt)
- Spring Data JPA / Hibernate
- PostgreSQL
- Spring for Apache Kafka

**Fraud service**
- Python, FastAPI
- confluent-kafka
- scikit-learn (Isolation Forest), NumPy, joblib

**Infrastructure**
- Kafka (KRaft mode) and PostgreSQL via Docker Compose
- OpenAPI / Swagger UI for API documentation

---

## Key design decisions

**Balances are derived, not stored.** Storing a balance column would create two sources of truth that can drift apart. Deriving the balance from the ledger guarantees that the balance and the transaction history can never disagree, and keeps the system fully auditable.

**Concurrency is handled with pessimistic locking.** Concurrent transfers from the same account could otherwise pass a balance check simultaneously and overdraw the account (a check-then-act race). Because financial consistency is critical and contention on a hot account is realistic, the sending account row is locked for the duration of the transfer.

**Idempotency has two layers.** An application-level check catches repeated requests cheaply; a database unique constraint on the idempotency key is the final guarantee, catching even two truly simultaneous requests that both pass the application check.

**Authentication vs. authorization are separate concerns.** JWT proves *who* you are (stateless — the identity travels in the token). Ownership checks in the service layer decide *what* you may do (you can only touch your own accounts). Proving identity does not grant access to every resource.

**Core stays synchronous, side effects go async.** Ledger writes must be atomic, so they stay in a single transaction. Audit and fraud are independent of a transfer's success, so they are driven by events — decoupling them and keeping the transfer path fast and resilient.

---

## Running it

### Prerequisites
- Docker & Docker Compose
- Java 21
- Python 3.11+ (for the fraud service)

### 1. Start infrastructure (Kafka + PostgreSQL)
```bash
docker compose up -d
```

### 2. Start the wallet service
```bash
./mvnw spring-boot:run
```

### 3. Start the fraud service
```bash
cd fraud-service
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python train_model.py     # trains and saves the ML model (once)
python consumer.py        # starts consuming transfer events
```

### 4. Explore the API
Open Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

---

## Example flow

1. **Register** a user: `POST /auth/register`
2. **Log in** to get a JWT: `POST /auth/login`
3. **Create an account** (owned by the user): `POST /accounts`
4. **Deposit** funds: `POST /accounts/{accountId}/deposit`
5. **Transfer** to another account: `POST /accounts/{fromAccountId}/transfer`
6. A **transfer event** is published to Kafka. The fraud service scores it; if it looks anomalous, the wallet service records it in `flagged_transfers`.
7. **Check the balance** (own account only): `GET /accounts/{accountId}/balance`

---

## Notes on the fraud model

The fraud service demonstrates a full ML pipeline: synthetic data generation, training an Isolation Forest anomaly-detection model, persisting it, loading it in the service, and scoring transfers in real time. The current model is intentionally simple — it scores on transaction amount and is trained on synthetic data — and is designed to be extended with real data and additional features (frequency, timing, recipient history, etc.).

---

## Status & roadmap

Implemented: double-entry ledger, concurrency control, idempotency, JWT auth and ownership-based authorization, global error handling, Kafka event-driven architecture, transfer audit, polyglot fraud microservice with ML scoring, OpenAPI docs.

Planned: full containerization of all services, CI/CD pipeline, structured logging and metrics (Prometheus), and expanded fraud features.
