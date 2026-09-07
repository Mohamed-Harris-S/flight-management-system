# Flight Management System

A backend REST API for flight search and booking, built with Java and Spring Boot. Supports airport and flight management, JWT-based authentication, role-based and ownership-based authorization, and concurrency-safe seat booking.

This project was built end-to-end — database design, entity relationships, business logic, security, and testing — as a demonstration of backend development fundamentals.

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
- **Flight search** — optional/partial filters (source, destination, date), paginated
- **User registration & login** — BCrypt password hashing, JWT issuance
- **Role-based authorization** — Admin vs. User permissions enforced via Spring Security
- **Ownership-based authorization** — users can only access their own bookings (Owner-or-Admin pattern), defended against IDOR
- **Booking creation** — with real-time seat availability checks
- **Concurrency-safe seat booking** — pessimistic database locking prevents overbooking when multiple users book the last seat simultaneously
- **Booking cancellation** — soft-cancel (status change, not deletion) with automatic seat restoration
- **Centralized exception handling** — consistent error response shape across the entire API
- **Input & business validation** — Bean Validation on DTOs, business-rule validation in the service layer
- **Pagination** — on flight listing and search endpoints

---

## Architecture

The project follows a standard layered architecture:

```
Controller  →  Service  →  Repository  →  Database
    ↓             ↓
   DTO        Business Logic
```

- **Controllers** — thin; handle HTTP concerns only (request parsing, status codes)
- **Services** — own all business logic, validation, and entity↔DTO mapping orchestration
- **Repositories** — Spring Data JPA interfaces; one custom JPQL query for flight search, one pessimistic-lock query for seat booking
- **Mappers** — dedicated `FlightMapper` and `BookingMapper` classes (composed together) handle entity-to-DTO conversion, keeping services free of mapping logic
- **DTOs** — separate Request and Response DTOs per entity; entities are never exposed directly through the API
- **Global exception handling** — a single `@RestControllerAdvice` converts all exceptions (business, validation, security) into a consistent JSON error shape

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.1 |
| Data Access | Spring Data JPA (Hibernate) |
| Database | MySQL 8.0 |
| Security | Spring Security, JWT (jjwt), BCrypt |
| Validation | Jakarta Bean Validation |
| Testing | JUnit 5, Mockito |
| Build Tool | Maven |
| API Testing | Postman |
| Version Control | Git / GitHub |

---

## Database Design

Four core entities:

```
User        — id, name, email, password (hashed), role

Airport     — id, code (unique), name, city, country

Flight      — id, flightNumber, sourceAirport (FK), destinationAirport (FK),
              departureTime, arrivalTime, totalSeats, availableSeats, status

Booking     — id, bookingReference (unique), user (FK), flight (FK),
              passengerName, passengerAge, status, bookedAt
```

**Key relationships & constraints:**
- `Flight` has **two separate** many-to-one relationships to `Airport` (source and destination) — a flight must reference two distinct airport records simultaneously, which a single foreign key field cannot express.
- `Flight` has a composite unique constraint on `(flightNumber, departureTime)` — the same flight number can recur on different dates (as real airlines do), but not twice on the same date/time.
- `Booking` has a many-to-one relationship to both `User` and `Flight`.
- All status fields (`FlightStatus`, `BookingStatus`, `Role`) are backed by Java enums, persisted via `EnumType.STRING` (never `ORDINAL`, to avoid silent data corruption if enum values are reordered).
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
- **Login** verifies credentials and issues a signed **JWT** containing the user's email and role.
- **JWT validation** happens in a custom `OncePerRequestFilter`, which populates Spring Security's context on every request — the filter never rejects requests directly; it only establishes identity, and `SecurityConfig` rules decide what's actually permitted.
- **Role-based rules** (e.g., only Admins can create flights) are enforced declaratively in `SecurityConfig`.
- **Ownership-based rules** (e.g., a user can only view their own bookings) require a data lookup that role-based config alone can't express, so they're enforced explicitly in the service layer — this is the project's specific defense against **IDOR** (Insecure Direct Object Reference): fetching a resource by ID and comparing its owner against the authenticated user before returning it.
- Login failures for both "email not found" and "wrong password" return an identical message, to avoid leaking which emails are registered (user enumeration protection).

---

## Concurrency Safety

The most concerning requirement in this project: **what happens if two users try to book the last seat on a flight at the same time?**

Booking creation and cancellation both use **pessimistic database locking** (`PESSIMISTIC_WRITE`) on the flight row, combined with `@Transactional` boundaries:

1. A request locks the flight row before reading its seat count.
2. A second concurrent request attempting the same flight must wait for the first transaction to fully commit.
3. Only after the lock is released does the second request see the updated seat count — preventing both requests from reading stale data and both succeeding when only one seat exists.

**Honest limitation:** unit tests verify that the correct repository methods and business logic are invoked, but they cannot verify the actual database-level locking behavior under real concurrent load — that would require an integration or load test against a live database, which will be implemented in future.

---

## Setup Instructions

### Prerequisites
- Java 21+ (developed on Java 25)
- MySQL 8.0+
- Maven (or use the included `mvnw` wrapper)

### 1. Clone the repository
```bash
git clone https://github.com/Mohamed-Harris-S/flight-management-system.git
cd flight-management-system
```

### 2. Create the database
```sql
CREATE DATABASE flight_management_db;
```

### 3. Set environment variables
```bash
# Windows PowerShell
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="a_long_random_secret_string_at_least_32_characters"

# macOS/Linux
export DB_PASSWORD="your_mysql_password"
export JWT_SECRET="a_long_random_secret_string_at_least_32_characters"
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. Hibernate will automatically create all tables on first run (`ddl-auto=update`).

---

## Sample API Requests

**Register**
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Mohamed Harris S",
  "email": "mohamedharris@example.com",
  "password": "password123"
}
```

**Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "mohamedharris@example.com",
  "password": "password123"
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
  "passengerName": "Mohamed Harris",
  "passengerAge": 23
}
```

---

## Testing

Unit tests cover the service layer — the layer where all business logic lives — using JUnit 5 and Mockito, with repositories mocked to isolate logic from the database.

**Coverage includes:**
- `AirportService` — creation happy path, duplicate-code rejection, not-found handling
- `FlightService` — full validation of `createFlight()`'s decision tree: same-airport rejection, invalid time-order rejection, missing-airport handling, and the happy path — each verified with both outcome assertions and interaction verification (`verify`/`verifyNoInteractions`) to prove the correct code path was taken, not just that an exception fired
- `BookingService` — seat-availability rejection, successful booking with seat decrement, already-cancelled rejection, and cancellation with seat restoration — including static mocking of Spring Security's context to simulate an authenticated user

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
