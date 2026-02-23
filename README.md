# TrayectorIA-backend

REST API backend for **TrayectorIA** — an AI-powered job networking platform connecting candidates with companies.

Built with **Kotlin + Spring Boot 3.5** following **Clean Architecture** principles.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 1.9 + Java 21 |
| Framework | Spring Boot 3.5.11 |
| Security | Spring Security + JWT (JJWT 0.12.6) |
| Persistence | Spring Data JPA + PostgreSQL |
| Migrations | Flyway |
| Mapping | MapStruct |
| AI | OpenAI API (Strategy pattern) |
| File Storage | Cloudinary |
| Build | Gradle (Kotlin DSL) |

---

## Project Structure

```
src/main/kotlin/com/edumora/trayectoria/
├── application/
│   ├── port/
│   │   ├── input/          # Use case interfaces (called by controllers)
│   │   └── output/         # Port interfaces (AI, storage, external services)
│   └── usecase/            # One class per use case
├── infrastructure/
│   ├── ai/
│   │   ├── config/         # OpenAI client bean
│   │   └── provider/       # AI Strategy implementations
│   ├── persistence/
│   │   ├── entity/         # JPA @Entity classes
│   │   └── repository/     # JpaRepository interfaces
│   ├── security/
│   │   ├── config/         # SecurityFilterChain, CORS
│   │   ├── jwt/            # JwtFilter, JwtService
│   │   └── service/        # UserDetailsService implementation
│   ├── storage/
│   │   └── cloudinary/     # File upload implementation
│   └── config/             # General beans
├── web/
│   ├── controller/         # @RestController classes
│   ├── dto/
│   │   ├── request/        # Incoming payloads
│   │   └── response/       # Outgoing payloads
│   └── mapper/             # MapStruct mappers (Entity ↔ DTO)
└── shared/
    ├── exception/          # GlobalExceptionHandler + custom exceptions
    └── util/               # Kotlin extensions, constants
```

---

## Environment Variables

```env
DB_URL=jdbc:postgresql://localhost:5432/trayectoria
DB_USERNAME=postgres
DB_PASSWORD=yourpassword
JWT_SECRET=your-256-bit-secret
JWT_EXPIRATION_MS=86400000
OPENAI_API_KEY=sk-...
CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_API_KEY=...
CLOUDINARY_API_SECRET=...
```

---

## API Reference — Phase 1

Base URL: `/api/v1`

### Auth
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/auth/register/candidate` | Register as candidate | Public |
| POST | `/api/v1/auth/register/company` | Register as company | Public |
| POST | `/api/v1/auth/login` | Login, returns JWT | Public |
| POST | `/api/v1/auth/refresh` | Refresh JWT token | Public |

### Candidate Profile
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/v1/candidates/me` | Get own profile | CANDIDATE |
| PUT | `/api/v1/candidates/me` | Update own profile | CANDIDATE |
| PATCH | `/api/v1/candidates/me/avatar` | Upload profile image | CANDIDATE |
| GET | `/api/v1/candidates/me/experience` | List work experience | CANDIDATE |
| POST | `/api/v1/candidates/me/experience` | Add work experience | CANDIDATE |
| PUT | `/api/v1/candidates/me/experience/{id}` | Update work experience | CANDIDATE |
| DELETE | `/api/v1/candidates/me/experience/{id}` | Delete work experience | CANDIDATE |
| GET | `/api/v1/candidates/me/education` | List education | CANDIDATE |
| POST | `/api/v1/candidates/me/education` | Add education entry | CANDIDATE |
| PUT | `/api/v1/candidates/me/education/{id}` | Update education | CANDIDATE |
| DELETE | `/api/v1/candidates/me/education/{id}` | Delete education | CANDIDATE |
| GET | `/api/v1/candidates/me/skills` | List candidate skills | CANDIDATE |
| POST | `/api/v1/candidates/me/skills` | Add skills | CANDIDATE |
| DELETE | `/api/v1/candidates/me/skills/{skillId}` | Remove a skill | CANDIDATE |
| GET | `/api/v1/candidates/me/languages` | List languages | CANDIDATE |
| POST | `/api/v1/candidates/me/languages` | Add language | CANDIDATE |
| DELETE | `/api/v1/candidates/me/languages/{language}` | Remove language | CANDIDATE |

### Company Profile
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/v1/companies/me` | Get own company profile | COMPANY |
| PUT | `/api/v1/companies/me` | Update company profile | COMPANY |
| PATCH | `/api/v1/companies/me/logo` | Upload company logo | COMPANY |

### Job Offers
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/v1/job-offers` | List active offers (filters: mode, type, skill) | Public |
| GET | `/api/v1/job-offers/{id}` | Get offer detail | Public |
| POST | `/api/v1/job-offers` | Create job offer | COMPANY |
| PUT | `/api/v1/job-offers/{id}` | Update job offer | COMPANY |
| DELETE | `/api/v1/job-offers/{id}` | Delete job offer | COMPANY |
| PATCH | `/api/v1/job-offers/{id}/status` | Change offer status | COMPANY |
| GET | `/api/v1/job-offers/mine` | List own company's offers | COMPANY |

### Applications
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/job-offers/{id}/apply` | Apply to a job offer | CANDIDATE |
| GET | `/api/v1/applications/mine` | List own applications | CANDIDATE |
| DELETE | `/api/v1/applications/{id}` | Withdraw application | CANDIDATE |
| GET | `/api/v1/job-offers/{id}/applications` | List applications for offer | COMPANY |
| PATCH | `/api/v1/applications/{id}/status` | Update application status | COMPANY |

### Saved Offers
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/v1/saved-offers` | List saved offers | CANDIDATE |
| POST | `/api/v1/saved-offers/{jobOfferId}` | Save a job offer | CANDIDATE |
| DELETE | `/api/v1/saved-offers/{jobOfferId}` | Remove saved offer | CANDIDATE |

### Skills
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/v1/skills` | List all skills (catalog) | Public |
| POST | `/api/v1/skills` | Create skill | ADMIN *(Phase 2)* |

### AI — Curriculum
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/curricula/generate` | Generate AI curriculum for an offer | CANDIDATE |
| GET | `/api/v1/curricula` | List own generated curricula | CANDIDATE |
| GET | `/api/v1/curricula/{id}` | Get specific curriculum | CANDIDATE |
| DELETE | `/api/v1/curricula/{id}` | Delete curriculum | CANDIDATE |

### AI — Simulated Interviews
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/interviews` | Start simulated interview | CANDIDATE |
| GET | `/api/v1/interviews` | List own interviews | CANDIDATE |
| GET | `/api/v1/interviews/{id}` | Get interview with messages | CANDIDATE |
| POST | `/api/v1/interviews/{id}/messages` | Send message to interview | CANDIDATE |
| PATCH | `/api/v1/interviews/{id}/complete` | Mark interview as complete | CANDIDATE |

---

## Phase 2 (Backlog)

- PDF curriculum export with ATS templates
- OAuth 2.0 with Google
- Candidate–offer matching engine
- External course recommendations (Coursera API)
- Company-required interviews before applying
- Interview result sharing with companies
- Notifications system
- Admin role and dashboard

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
./gradlew bootRun
```

---

## Running Tests

```bash
./gradlew test
```

---

## Autor

**Leandro Mora Corrales**

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.