# Bus Reservation System — Spring Boot Backend

## Structure
```
backend/
├── pom.xml
├── src/main/java/com/busreservation/
│   ├── BusReservationApplication.java   (main entry point)
│   ├── model/          (JPA entities — mirrors MySQL schema)
│   ├── repository/     (Spring Data JPA repositories)
│   ├── dto/             (request/response objects)
│   ├── security/        (JwtUtil, JwtFilter)
│   ├── config/           (SecurityConfig, CorsConfig)
│   ├── controller/       (REST API endpoints)
│   └── exception/        (GlobalExceptionHandler)
└── src/main/resources/
    └── application.properties
```

## Before running
1. Make sure MySQL is running and the `bus_reservation_system` database + tables already exist (run your schema.sql first).
2. Open `src/main/resources/application.properties` and update:
   - `spring.datasource.username`
   - `spring.datasource.password`

## Run
```bash
cd backend
mvn spring-boot:run
```

Backend will start on **http://localhost:5000**.

## Key endpoints
| Method | Endpoint | Auth required |
|---|---|---|
| POST | /api/auth/signup | No |
| POST | /api/auth/login | No |
| GET | /api/buses/search | No |
| GET | /api/seats/{scheduleId} | No |
| GET | /api/routes/{routeId}/points | No |
| POST | /api/bookings | Yes (Bearer token) |
| GET | /api/bookings/user/{userId} | Yes |
| PUT | /api/bookings/{id}/cancel | Yes |
| POST | /api/seat-locks | Yes |
| GET/POST | /api/reviews | No/Yes |
| /api/admin/** | Yes (ADMIN role only) |

## Notes
- `spring.jpa.hibernate.ddl-auto=update` will auto-create/verify tables to match the entities on startup — safe to run even on an existing database.
- CORS is configured for `http://localhost:5173` (default Vite dev server) in `CorsConfig.java` — update this if your frontend runs on a different port.
- The `POST /api/bookings` endpoint now returns full structured booking details (bus, route, seats, fare) instead of a plain string, matching what `BookingConfirmation.jsx` expects on the frontend.
