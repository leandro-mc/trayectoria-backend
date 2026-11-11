# TrayectorIA — Backend

> Read in English: [README.md](README.md)

API REST de **TrayectorIA**, una plataforma de networking laboral con IA que conecta candidatos con empresas. Los candidatos arman su perfil, obtienen un CV generado con IA adaptado a una oferta y practican entrevistas simuladas; las empresas publican ofertas y gestionan postulaciones. Frontend complementario: [trayectoria-web](https://github.com/leandro-mc/trayectoria-web).

## Funciones

- **Dos roles** (candidato y empresa) con autenticación JWT y refresh tokens
- **Perfil del candidato:** experiencia, educación, habilidades e idiomas
- **Ofertas de trabajo** con postulaciones y seguimiento de estado; ofertas guardadas
- **CVs generados con IA** a partir del perfil del candidato (Spring AI + OpenAI)
- **Entrevistas simuladas con IA** según las instrucciones de la oferta, en formato de chat
- Endpoint de **estadísticas** para empresas
- Entrevistas por voz: planificadas (el endpoint solo expone un indicador de disponibilidad)

## Tecnologías

- Kotlin 1.9, Java 21, Spring Boot 3.5
- Spring Security + JWT, acceso por roles
- Spring Data JPA, PostgreSQL, migraciones Flyway, MapStruct
- Spring AI con la API de OpenAI
- SpringDoc OpenAPI (Swagger UI)
- Pruebas de integración con JUnit 5 + Testcontainers
- Gradle (Kotlin DSL), Docker, configuración de despliegue en Railway

## Cómo ejecutarlo

Requisitos: JDK 21 y Docker.

```bash
git clone https://github.com/leandro-mc/trayectoria-backend.git
cd trayectoria-backend
cp .env.example .env     # BD, secreto JWT, clave de OpenAI
docker-compose up -d     # solo PostgreSQL
./gradlew bootRun        # Windows: gradlew.bat bootRun
```

Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
Pruebas: `./gradlew test` (requiere Docker para Testcontainers)

## Estructura

```
src/main/kotlin/com/edumora/trayectoria/
├── application/     # Casos de uso (uno por clase) y puertos de salida (IA)
├── infrastructure/  # Entidades/repositorios JPA, seguridad (JWT), Spring AI, config
├── web/             # Controllers, DTOs y mappers MapStruct
└── shared/          # Manejo de excepciones y utilidades
```
