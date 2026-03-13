# ================================================================
#  Dockerfile — trayectoria-backend
#  Multi-stage build: keeps the final image small
#  Usage: docker build -t trayectoria-backend .
#         docker run -p 8080:8080 --env-file .env trayectoria-backend
# ================================================================

# ── Stage 1: Build ────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and config first (layer cache — only re-downloads
# dependencies if build.gradle.kts or gradle files actually change)
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x gradlew

# Download dependencies (cached layer as long as build.gradle.kts doesn't change)
RUN ./gradlew dependencies --no-daemon

# Copy source and build
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# ── Stage 2: Run ──────────────────────────────────────────────────
# JRE only (no compiler) — much smaller image
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy only the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]