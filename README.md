# Flight Management System

A backend REST API for flight search and booking, built with Java and Spring Boot. Supports airport and flight management, JWT-based authentication, role-based and ownership-based authorization, and concurrency-safe seat booking.

This project was built end-to-end — database design, entity relationships, business logic, security, and testing — as a demonstration of backend development fundamentals.


**Swagger UI:** https://flight-management-system-production-75f5.up.railway.app/swagger-ui.html


---

## Problem Statement

Airlines need a system where:
- Admins can manage airports and flights.
- Users can search for flights and book seats.
- Seat availability must stay accurate even when multiple users try to book the same flight at the same time.
- Users should only be able to view or cancel their own bookings — not anyone else's.

This project implements that system as a REST API, with particular attention to the business rules and security concerns a real booking platform would need to get right.

---

## Features

- **Airport management** — full CRUD (Admin-only for writes, public reads)
- **Flight management** — create, update status, search by source/destination/date, paginated listing
- **Flight search** — optional/partial filters (source, destination, date), paginated, built on a single parameterized JPQL query rather than one method per filter combination
- **User registration & login** — BCrypt password hashing, JWT issuance
- **Role-based authorization** — Admin vs. User permissions enforced via Spring Security
- **Ownership-based authorization** — users can only access their own bookings (Owner-or-Admin pattern), defended against IDOR
- **Booking creation** — with real-time seat availability checks
- **Concurrency-safe seat booking** — pessimistic database locking prevents overbooking when multiple users book the last seat simultaneously
- **Booking cancellation** — soft-cancel (status change, not deletion) with automatic seat restoration
- **Centralized exception handling** — consistent error response shape across the entire API
- **Input & business validation** — Bean Validation on DTOs, business-rule validation in the service layer
- **Pagination** — on flight listing and search endpoints
- **Interactive API documentation** — Swagger/OpenAPI, with JWT bearer-token support built in
- **Containerized** — multi-stage Docker build
- **Deployed and live** — running on Railway (app + MySQL)
  
---

## Architecture

The project follows a standard layered architecture:

<img width="2272" height="573" alt="Architecture-Diagram drawio" src="https://github.com/user-attachments/assets/8dd3861f-7682-4212-9cfc-225baf3bf422" />


- **Controllers** — thin; handle HTTP concerns only (request parsing, status codes)
- **Services** — own all business logic and validation
- **Mappers** — dedicated `FlightMapper` and `BookingMapper` classes (composed together) handle entity-to-DTO conversion, keeping services free of mapping logic
- **Repositories** — Spring Data JPA interfaces; one custom JPQL query for flight search, one pessimistic-lock query for seat booking
- **DTOs** — separate Request and Response DTOs per entity; entities are never exposed directly through the API
- **Global exception handling** — a single `@RestControllerAdvice` converts all exceptions (business, validation, security) into a consistent error response

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1 |
| Data Access | Spring Data JPA (Hibernate) |
| Database | MySQL 8.0 |
| Security | Spring Security, JWT (jjwt), BCrypt |
| Validation | Jakarta Bean Validation |
| Testing | JUnit 5, Mockito |
| API Documentation | springdoc-openapi / Swagger UI |
| Build Tool | Maven |
| Containerization | Docker (multi-stage build) |
| Deployment | Railway |
| Version Control | Git / GitHub |

---

## Database Schema

Four core entities: `User`, `Airport`, `Flight`, `Booking`.

<img width="1482" height="622" alt="ER_DIAGRAM drawio" src="https://github.com/user-attachments/assets/c9425da1-2f75-4231-b558-501802a531b6" />


**Key relationships & constraints:**
- `Flight` has **two separate** many-to-one relationships to `Airport` (source and destination) — a flight must reference two distinct airport records simultaneously, which a single foreign key field cannot express.
- `Flight` has a composite unique constraint on `(flightNumber, departureTime)` — the same flight number can recur on different dates (as real airlines do), but not twice on the same date/time.
- `Booking` has a many-to-one relationship to both `User` and `Flight`.
- All status fields (`FlightStatus`, `BookingStatus`, `Role`) are backed by Java enums, persisted via `EnumType.STRING` (never `ORDINAL`, to avoid silent data corruption if enum values are ever reordered).
- Referential integrity enforced via foreign keys at the database level, in addition to service-layer checks (defense in depth).

---

## API Endpoints

**Auth**
```
POST   /api/auth/register              Public
POST   /api/auth/login                 Public
```

**Airport**
```
POST   /api/airports                   Admin
GET    /api/airports                   Public
GET    /api/airports/{id}              Public
PUT    /api/airports/{id}              Admin
DELETE /api/airports/{id}              Admin
```

**Flight**
```
POST   /api/flights                    Admin
GET    /api/flights                    Admin (paginated)
GET    /api/flights/{id}               Public
GET    /api/flights/search             Public (paginated, optional filters)
PATCH  /api/flights/{id}               Admin (status update)
```

**Booking**
```
POST   /api/bookings                   Authenticated User
GET    /api/bookings/{id}              Owner or Admin
PATCH  /api/bookings/{id}/cancel       Owner or Admin
GET    /api/users/{userId}/bookings    Owner or Admin
```

---

## Authentication & Authorization

- **Passwords** are hashed with BCrypt before storage — never stored or logged in plain text.
- **Login** verifies credentials and issues a signed **JWT** containing the user's email and role. Both "email not found" and "wrong password" return an identical error message, to avoid leaking which emails are registered.
- **JWT validation** happens in a custom `OncePerRequestFilter`, which populates Spring Security's context on every request — the filter never rejects requests directly; it only establishes identity, and `SecurityConfig` rules decide what's actually permitted.
- **Role-based rules** (e.g., only Admins can create flights) are enforced declaratively in `SecurityConfig`.
- **Ownership-based rules** (e.g., a user can only view their own bookings) require a data lookup that role-based config alone can't express, so they're enforced explicitly in the service layer — this is the project's specific defense against **IDOR** (Insecure Direct Object Reference): fetching a resource by ID and comparing its owner against the authenticated user before returning it.

---

## Concurrency Safety

The hardest requirement in this project: **what happens if two users try to book the last seat on a flight at the same time?**

Booking creation and cancellation both use **pessimistic database locking** (`PESSIMISTIC_WRITE`) on the flight row, combined with `@Transactional` boundaries:

1. A request locks the flight row before reading its seat count.
2. A second concurrent request attempting the same flight must wait for the first transaction to fully commit.
3. Only after the lock is released does the second request see the updated seat count — preventing both requests from reading stale data and both succeeding when only one seat exists.

**Honest limitation:** unit tests verify that the correct repository methods and business logic are invoked, but they cannot verify the actual database-level locking behavior under real concurrent load — that would require an integration or load test against a live database. An attempt was made to add this using Testcontainers; it hit a Windows-specific Docker named-pipe compatibility issue that wasn't resolved in the time available. The test infrastructure exists but is currently disabled — see Future Improvements.

---

## Setup Instructions

### Prerequisites
- Java 21+
- MySQL 8.0+
- Maven (or use the included `mvnw` wrapper)
- Docker (optional, for containerized run)

### Option A — Run locally

**1. Clone the repository**
```bash
git clone https://github.com/Mohamed-Harris-S/flight-management-system.git
cd flight-management-system
```

**2. Create the database**
```sql
CREATE DATABASE flight_management_db;
```

**3. Set environment variables**
```bash
# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE="local"
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="a_long_random_secret_string_at_least_32_characters"

# macOS/Linux
export SPRING_PROFILES_ACTIVE="local"
export DB_PASSWORD="your_mysql_password"
export JWT_SECRET="a_long_random_secret_string_at_least_32_characters"
```

**4. Run the application**
```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. Hibernate automatically creates all tables on first run (`ddl-auto=update`).

### Option B — Run with Docker

**Build the image:**
```bash
docker build -t flight-management-system .
```

**Run it** (pointing at a local MySQL instance — `host.docker.internal` lets the container reach MySQL running on your host machine):
```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=local \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/flight_management_db \
  -e DB_PASSWORD=your_password \
  -e JWT_SECRET=your_secret \
  flight-management-system
```

---

## Sample API Requests

**Register**
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "securepass123"
}
```

**Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "jane@example.com",
  "password": "securepass123"
}
```
Returns a JWT to use as `Authorization: Bearer <token>` on subsequent requests.

**Search flights**
```http
GET /api/flights/search?source=JFK&destination=MAA&date=2026-09-01&page=0&size=10
```

**Create a booking**
```http
POST /api/bookings
Authorization: Bearer <token>
Content-Type: application/json

{
  "flightId": 1,
  "passengerName": "Jane Doe",
  "passengerAge": 29
}
```

---

## Testing

Unit tests cover the service layer — the layer where all business logic lives — using JUnit 5 and Mockito, with repositories mocked to isolate logic from the database.

**Coverage includes:**
- `AirportService` — creation happy path, duplicate-code rejection, not-found handling
- `FlightService` — full validation of `createFlight()`'s decision tree: same-airport rejection, invalid time-order rejection, missing-airport handling, and the happy path — each verified with both outcome assertions and interaction verification (`verify` / `verifyNoInteractions`) to prove the correct code path was taken, not just that an exception fired
- `BookingService` — seat-availability rejection, successful booking with seat decrement, already-cancelled rejection, and cancellation with seat restoration — including static mocking of Spring Security's context to simulate an authenticated user

**12 unit tests total.** Manual end-to-end testing of every endpoint (including negative cases: wrong roles, missing tokens, cross-user access attempts, malformed input) was performed via Postman and Swagger throughout development, both locally and against the live deployment.

---

## Future Improvements

- **One passenger per booking** — the data model supports this cleanly; multi-passenger bookings would require a separate `Passenger` entity and batch seat-decrement logic.
- **No payment integration** — booking status is tracked (`CONFIRMED`/`CANCELLED`) without simulating real payment processing, to avoid unnecessary third-party integration complexity disproportionate to this project's goals.
- **No flight status state machine** — status can be updated to any of the five valid enum values without transition rules (e.g., an `ARRIVED` flight could technically be set back to `SCHEDULED`). A production system would enforce a proper state machine.
- **401 vs. 403 nuance** — Spring Security's default behavior returns `403` for both "no credentials provided" and "insufficient role," where `401` would be more semantically correct for the former. Fixing this would require a custom `AuthenticationEntryPoint`.
- **Integration/concurrency testing** — unit tests verify business logic in isolation; they cannot verify that pessimistic locking behaves correctly under genuine concurrent load against a real database.

---

## Author

Built by Mohamed Harris, focused on strengthening backend development fundamentals.
