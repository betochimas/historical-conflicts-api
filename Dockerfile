# --- Build stage: compile + package the executable jar ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Resolve dependencies first so this layer caches across source-only changes.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B dependency:go-offline

# Build the jar. Skip tests in the image — they need Docker (Testcontainers) and run in CI.
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

# --- Runtime stage: JRE only ---
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Run as a non-root user.
RUN useradd --system --no-create-home --uid 1001 appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

# Platform injects PORT; the app binds to it via server.port=${PORT:8080}.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
