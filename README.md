# Event Booking Platform

A small microservices system for creating events and booking seats.

## Demo video

<video src="video/demo.mp4" controls width="720">
  Your browser can't play this video. Download it directly: <a href="video/demo.mp4">video/demo.mp4</a>
</video>

Shows: running the app, publishing an event, booking a seat, an error case (no seats left), and the JWT token. No audio.

## Stack

- **event-service** (Spring Boot 3, Java 21, port 8081) — manage events, state machine `DRAFT -> PUBLISHED -> CANCELLED`, user registration/login (JWT), Liquibase, Swagger
- **booking-service** (Spring Boot 3, Java 21, port 8082) — book seats, track availability, calls event-service, Liquibase, Swagger
- **frontend** (React + TypeScript + Vite, port 3000)
- **PostgreSQL** (shared database, separate tables per service)

## Run with Docker Compose (recommended)

Requires Docker Desktop.

```bash
docker compose up --build
```

- Frontend: http://localhost:3000
- event-service Swagger: http://localhost:8081/swagger-ui.html
- booking-service Swagger: http://localhost:8082/swagger-ui.html

## Run locally without Docker (fallback)

Each service has a `local` Spring profile that uses a file-based H2 database instead of PostgreSQL, so it can run without any extra install.

```bash
# event-service
cd event-service
mvn spring-boot:run -Dspring-boot.run.profiles=local

# booking-service
cd booking-service
mvn spring-boot:run -Dspring-boot.run.profiles=local

# frontend
cd frontend
npm install
npm run dev
```

## Authentication

- `POST /api/auth/register` `{ "username", "password", "role": "ADMIN" | "USER" }`
- `POST /api/auth/login` `{ "username", "password" }`
- Both return a JWT. Send it as `Authorization: Bearer <token>` on write requests.
- Only **ADMIN** can create/publish/cancel events.
- Any authenticated user can book seats, max **2 seats per user per event**.
- Tokens expire after 60 minutes.

## Unified error format

```json
{ "status": 409, "errorCode": "NO_SEATS_AVAILABLE", "message": "..." }
```

## Tests

```bash
cd event-service && mvn test
cd booking-service && mvn test
```
