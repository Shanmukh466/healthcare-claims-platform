# Healthcare Claims Processing Platform

A production-grade, HIPAA-compliant healthcare claims processing system built with Java 17, Spring Boot 3.x, Apache Kafka, and PostgreSQL.

## Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   REST API      │────▶│  Claims Service  │────▶│   PostgreSQL    │
│  (JWT Auth)     │     │  (Business Logic)│     │   (Persistence) │
└─────────────────┘     └────────┬────────┘     └─────────────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │  Apache Kafka   │
                         │  Event Streaming│
                         └────────┬────────┘
                                  │
                    ┌─────────────┴──────────────┐
                    ▼                             ▼
           ┌──────────────┐             ┌──────────────────┐
           │  Claims      │             │   Notification   │
           │  Processor   │             │   Service        │
           └──────────────┘             └──────────────────┘
```

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Primary language |
| Spring Boot | 3.2.0 | Application framework |
| Spring Security | 6.x | JWT Authentication & RBAC |
| Apache Kafka | 3.x | Event streaming |
| PostgreSQL | 15 | Primary database |
| Docker | Latest | Containerization |
| Maven | 3.x | Build tool |
| JUnit 5 | 5.x | Unit testing |
| Swagger/OpenAPI | 3.x | API documentation |

## Features

- **Claims Processing** — Submit, review, approve, and reject healthcare claims
- **JWT Authentication** — Secure API access with role-based authorization
- **Kafka Event Streaming** — Real-time claim events published to Kafka topics
- **HIPAA Compliance** — Audit trails on every claim state change
- **Pagination & Filtering** — Efficient data retrieval with sorting
- **OpenAPI Docs** — Full Swagger UI available at `/swagger-ui.html`
- **Health Monitoring** — Spring Actuator endpoints for observability
- **Docker Ready** — Full docker-compose setup with one command

## Quick Start

### Prerequisites
- Java 17+
- Docker and Docker Compose
- Maven 3.x

### Run with Docker (recommended)

```bash
# Clone the repo
git clone https://github.com/YOUR_USERNAME/healthcare-claims-platform.git
cd healthcare-claims-platform

# Start everything — PostgreSQL, Kafka, and the app
docker-compose up -d

# Check logs
docker-compose logs -f app
```

The app will be running at `http://localhost:8080`

### Run locally

```bash
# Start only infrastructure
docker-compose up -d postgres kafka

# Run the app
mvn spring-boot:run
```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/login` | Login and get JWT token |
| POST | `/api/v1/auth/register` | Register new user |

### Claims
| Method | Endpoint | Description | Role Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1/claims` | Submit new claim | PROVIDER, ADMIN |
| PUT | `/api/v1/claims/process` | Approve or reject claim | CLAIMS_PROCESSOR, ADMIN |
| GET | `/api/v1/claims` | Get all claims (paginated) | ADMIN, AUDITOR |
| GET | `/api/v1/claims/{id}` | Get claim by ID | ADMIN, AUDITOR |
| GET | `/api/v1/claims/number/{claimNumber}` | Get by claim number | All authenticated |
| GET | `/api/v1/claims/patient/{patientId}` | Get claims by patient | All authenticated |
| GET | `/api/v1/claims/status/{status}` | Get claims by status | CLAIMS_PROCESSOR, ADMIN |

## Example API Usage

### 1. Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### 2. Submit a Claim
```bash
curl -X POST http://localhost:8080/api/v1/claims \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "PAT-001",
    "patientName": "John Doe",
    "providerId": "PROV-001",
    "providerName": "Dallas Medical Center",
    "insuranceMemberId": "MBR-12345",
    "diagnosisCode": "J06.9",
    "procedureCode": "99213",
    "billedAmount": 350.00,
    "serviceDate": "2024-03-15",
    "claimType": "MEDICAL"
  }'
```

### 3. Approve a Claim
```bash
curl -X PUT http://localhost:8080/api/v1/claims/process \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "claimId": "CLAIM_UUID_HERE",
    "newStatus": "APPROVED",
    "approvedAmount": 280.00
  }'
```

## Kafka Topics

| Topic | Description |
|-------|-------------|
| `claims-submitted` | New claim submitted |
| `claims-processed` | Claim status updated |
| `claims-approved` | Claim approved for payment |
| `claims-rejected` | Claim rejected with reason |

View Kafka topics at: `http://localhost:8090` (Kafka UI)

## User Roles

| Role | Permissions |
|------|-------------|
| ADMIN | Full access |
| CLAIMS_PROCESSOR | Submit, process, view claims |
| PROVIDER | Submit claims |
| AUDITOR | View-only access |

## Running Tests

```bash
mvn test
```

## API Documentation

After starting the app, visit:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Roadmap
- Add claim appeal workflow
- Integrate with insurance provider APIs
- Add real-time claim status notifications
- Build admin dashboard with React

## Project Structure

```
src/
├── main/
│   ├── java/com/healthcare/claims/
│   │   ├── ClaimsPlatformApplication.java
│   │   ├── config/          # Security, OpenAPI config
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── exception/       # Custom exceptions + handler
│   │   ├── kafka/           # Kafka producer & consumer
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # Spring Data repositories
│   │   └── service/         # Business logic
│   └── resources/
│       └── application.yml
└── test/
    └── java/com/healthcare/claims/
        └── ClaimsServiceTest.java
```

## Author

**Shanmukha Sai Ram Tummuri**
- LinkedIn: [linkedin.com/in/shanmukh-t-26a2aa400](https://www.linkedin.com/in/shanmukh-t-26a2aa400/)
- Email: shanmukhsairam84@gmail.com
