
# Release Manager

## Overview
A Kotlin and Spring Boot application that manages
deployment versions across multiple environments.

## Technologies
- Kotlin
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Docker Compose
- Gradle

## Prerequisites
- JDK 21 or later
- Docker Desktop

## Running the application

1. Start PostgreSQL:

   docker compose up -d

2. Start Spring Boot:

   ./gradlew bootRun

   On Windows:
   gradlew.bat bootRun

## API Endpoints

### POST /deploy

Registers a service deployment and returns
the current SystemVersion.

### GET /services?systemVersion=1

Returns the service versions associated
with the requested SystemVersion.

## Environment handling

The environment is resolved from the request hostname.

Examples:
- localhost: development
- staging.localhost: staging


## Automated Tests

The project includes five integration test scenarios covering the core deployment versioning logic.

Run the tests using:

```bash
./gradlew test
```

On Windows:

```powershell
.\gradlew.bat test
```

### Test Scenarios

**1. First Deployment**

Verifies that deploying a service for the first time creates the initial SystemVersion.

| Deployment | Expected SystemVersion |
|---|---:|
| Service A v1 | 1 |

**2. Deployment of a New Service**

Verifies that deploying a new service creates a new SystemVersion and preserves the versions of previously deployed services.

| Deployment | Expected SystemVersion |
|---|---:|
| Service A v1 | 1 |
| Service B v1 | 2 |

SystemVersion 2 must contain both Service A v1 and Service B v1.

**3. Updating an Existing Service**

Verifies that changing the deployed version of an existing service creates a new SystemVersion.

| Deployment | Expected SystemVersion |
|---|---:|
| Service A v1 | 1 |
| Service A v2 | 2 |

The historical snapshot for SystemVersion 1 must remain unchanged.

**4. Deploying an Unchanged Service Version**

Verifies that deploying a service with the same version it is already running does not create a new SystemVersion.

| Deployment | Expected SystemVersion |
|---|---:|
| Service A v1 | 1 |
| Service A v1 | 1 |

The second deployment must return the existing SystemVersion.

**5. Rolling Back to a Previous Service Version**

Verifies that rolling back a service to a previously deployed version creates a new SystemVersion rather than modifying an existing historical snapshot.

| Deployment | Expected SystemVersion |
|---|---:|
| Service A v1 | 1 |
| Service B v1 | 2 |
| Service A v2 | 3 |
| Service A v1 (rollback) | 4 |

The final snapshot must contain Service A v1 and Service B v1, while all previous SystemVersion snapshots remain unchanged.

### Test Isolation

Each test uses an independent environment identifier to prevent interference between test scenarios.

Tests run within Spring-managed transactions, which are rolled back after execution to avoid leaving test records in the database.
