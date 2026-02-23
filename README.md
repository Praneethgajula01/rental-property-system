# Rental Property System (Backend)

Spring Boot backend for a rental listing platform with role-based access, admin moderation, booking lifecycle, and search APIs.

This project started as a simple rental property listing system and evolved into a full backend architecture with clear modules (Controller, Service, Repository, Entity), secure JWT auth, and production-style workflows suitable for a major college project.

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- H2 / MySQL / PostgreSQL
- OpenAPI (Swagger UI)
- JUnit 5 + Mockito

## Implemented Features
- Authentication:
  - Register (`USER` / `HOST`)
  - Login with JWT
  - Admin self-registration blocked
- Role-based access:
  - `USER`: browse + create/cancel own bookings
  - `HOST`: create listings + view own listings/host bookings
  - `ADMIN`: approve/reject listings + confirm bookings + admin views
- Property moderation:
  - `PENDING`, `APPROVED`, `REJECTED`
  - Public listing endpoints return approved items only
- Booking lifecycle:
  - `REQUESTED`, `CONFIRMED`, `CANCELLED`
  - Date-based booking with overlap validation
- Search and discovery:
  - Query + filters + sorting + pagination on properties

## API Docs (Swagger)
- UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Use `Authorize` button in Swagger and pass `Bearer <jwt-token>` for protected endpoints.

## Key Endpoints

### Auth
- `POST /auth/register`
- `POST /auth/login`

### Properties
- `GET /properties`
- `GET /properties/search`
- `GET /properties/available`
- `GET /properties/{id}`
- `POST /properties` (HOST/ADMIN)
- `GET /properties/host/my` (HOST/ADMIN)
- `GET /properties/admin/all` (ADMIN)
- `GET /properties/admin/pending` (ADMIN)
- `POST /properties/{id}/approve` (ADMIN)
- `POST /properties/{id}/reject` (ADMIN)

### Bookings
- `POST /bookings`
- `GET /bookings/my`
- `GET /bookings/host/my` (HOST/ADMIN)
- `GET /bookings/admin/all` (ADMIN)
- `POST /bookings/{id}/confirm` (ADMIN)
- `POST /bookings/{id}/cancel`

## Local Run
```bash
cd rental
./mvnw spring-boot:run
```

On Windows PowerShell:
```powershell
cd rental
.\mvnw.cmd spring-boot:run
```

Default DB is in-memory H2 via `src/main/resources/application.properties`.

## Tests
Run tests:
```bash
./mvnw test
```

Included tests cover:
- Booking lifecycle validation (service layer)
- Search/pagination and listing creation behavior (service layer)

## Environment Notes
- JWT secret is currently in code for local project use.
- For production-like setup, move secrets to environment variables and secure config.

## Project Value (Resume)
This project demonstrates:
- Full-stack ready REST API design
- Authentication and authorization patterns
- Moderation workflow modeling
- Booking lifecycle state management
- Search/pagination API design
- Automated testing and API documentation
