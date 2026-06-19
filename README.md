# TrayectorIA-backend

REST API backend for **TrayectorIA** — an AI-powered job networking platform connecting candidates with companies.

Built with **Kotlin + Spring Boot 3.5** following **Clean Architecture** principles.

---

## Tech Stack

| Layer | Technology                          |
|---|-------------------------------------|
| Language | Kotlin 1.9 + Java 21                |
| Framework | Spring Boot 3.5.11                  |
| Security | Spring Security + JWT (JJWT 0.12.6) |
| Persistence | Spring Data JPA + PostgreSQL        |
| Migrations | Flyway                              |
| Mapping | MapStruct                           |
| AI | Spring AI + OpenAI API              |
| Documentation | SpringDoc OpenAPI 3 (Swagger UI)    |
| Testing | JUnit 5 + Testcontainers            |
| Build | Gradle (Kotlin DSL)                 |

[//]: # (| File Storage | Cloudinary &#40;Phase 2&#41;                | )
---

## Getting Started

```bash
# Clone and run
git clone  https://github.com/leandro-mc/trayectoria-backend.git
cd trayectoria-backend

# Copy and fill env vars
cp .env.example .env

# Run with Docker (DB only)
docker-compose up -d

# Start the app
gradlew bootRun
```

---

### API Documentation (Swagger UI)
```
http://localhost:8080/api/swagger-ui/index.html
```

---

## Environment Variables

```env
DB_URL=jdbc:postgresql://localhost:5434/trayectoria
DB_USERNAME=postgres
DB_PASSWORD=yourpassword
JWT_SECRET=your-256-bit-secret
JWT_EXPIRATION_MS=86400000
JWT_REFRESH_EXPIRATION_MS=604800000
OPENAI_API_KEY=sk-...
OPENAI_MODEL=gpt-4o-mini
CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_API_KEY=...
CLOUDINARY_API_SECRET=...
```

---

## Project Structure

```
src/main/kotlin/com/edumora/trayectoria/
├── application/
│   ├── port/
│   │   └── output/         # Port interfaces (AI, storage, external services)
│   └── usecase/            # One class per use case
├── infrastructure/
│   ├── ai/                 # Spring AI / OpenAI implementation
│   ├── persistence/
│   │   ├── entity/         # JPA @Entity classes
│   │   └── repository/     # JpaRepository interfaces
│   ├── security/
│   │   ├── config/         # SecurityFilterChain, CORS
│   │   ├── jwt/            # JwtFilter, JwtService
│   │   └── service/        # UserDetailsService implementation
│   ├── storage/            # Cloudinary file upload
│   └── config/             # OpenAPI config, general beans
├── web/
│   ├── controller/         # @RestController classes
│   ├── dto/
│   │   ├── request/        # Incoming payloads
│   │   └── response/       # Outgoing payloads
│   └── mapper/             # MapStruct mappers (Entity ↔ DTO)
└── shared/
    ├── exception/          # GlobalExceptionHandler + custom exceptions
    └── util/               # Kotlin extensions, SecurityUtils
```

---

## Running Tests

```bash
./gradlew test
```

---

[//]: # (## Phase 2 &#40;Backlog&#41;)

[//]: # ()
[//]: # (- PDF curriculum export with ATS templates)

[//]: # (- OAuth 2.0 with Google)

[//]: # (- Candidate–offer matching engine)

[//]: # (- External course recommendations &#40;Coursera API&#41;)

[//]: # (- Company-required interviews before applying)

[//]: # (- Interview result sharing with companies)

[//]: # (- Notifications system)

[//]: # (- Admin role and dashboard)

[//]: # ()
[//]: # (---)

## Autor

**Leandro Mora Corrales**
[linkedin.com/in/leandromora](https://linkedin.com/in/leandromora)

## Licencia

MIT License — see [LICENSE](LICENSE) for details.