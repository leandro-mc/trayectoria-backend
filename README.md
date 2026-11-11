# TrayectorIA — Backend

> Leer en español: [README.es.md](README.es.md)

REST API for **TrayectorIA**, an AI-powered job networking platform that connects candidates with companies. Candidates build a profile, get an AI-generated CV tailored to an offer and practice mock interviews; companies publish offers and manage applications. Companion frontend: [trayectoria-web](https://github.com/leandro-mc/trayectoria-web).

## Features

- **Two roles** (candidate and company) with JWT authentication and refresh tokens
- **Candidate profile:** experience, education, skills and languages
- **Job offers** with applications and status tracking; saved offers
- **AI-generated CVs** from the candidate profile (Spring AI + OpenAI)
- **AI mock interviews** based on the instructions of a job offer, with chat-style messages
- **Company stats** dashboard endpoint
- Voice interviews: planned (the endpoint only exposes a feature flag)

## Tech stack

- Kotlin 1.9, Java 21, Spring Boot 3.5
- Spring Security + JWT, role-based access
- Spring Data JPA, PostgreSQL, Flyway migrations, MapStruct
- Spring AI with the OpenAI API
- SpringDoc OpenAPI (Swagger UI)
- JUnit 5 + Testcontainers integration tests
- Gradle (Kotlin DSL), Docker, Railway deploy config

## Getting started

Requirements: JDK 21 and Docker.

```bash
git clone https://github.com/leandro-mc/trayectoria-backend.git
cd trayectoria-backend
cp .env.example .env     # DB, JWT secret, OpenAI key
docker-compose up -d     # PostgreSQL only
./gradlew bootRun        # Windows: gradlew.bat bootRun
```

Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
Tests: `./gradlew test` (requires Docker for Testcontainers)

## Structure

```
src/main/kotlin/com/edumora/trayectoria/
├── application/     # Use cases (one class each) and output ports (AI)
├── infrastructure/  # JPA entities/repositories, security (JWT), Spring AI, config
├── web/             # Controllers, DTOs and MapStruct mappers
└── shared/          # Exception handling and utilities
```
