# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Install parent POMs
COPY pom.xml .
COPY infra/pom.xml ./infra/
COPY infra/config-server/pom.xml ./infra/config-server/

RUN --mount=type=cache,target=/root/.m2 mvn install -N
RUN --mount=type=cache,target=/root/.m2 mvn install -N -f infra/pom.xml

# Download and cache dependencies
RUN --mount=type=cache,target=/root/.m2 mvn -f infra/config-server/pom.xml dependency:go-offline

# Copy configs
WORKDIR /app/infra/config-server/src/main/resources/config/telemetry
COPY telemetry/collector/src/main/resources/application.yaml ./collector/
COPY telemetry/aggregator/src/main/resources/application.yaml ./aggregator/
COPY telemetry/analyzer/src/main/resources/application.yaml ./analyzer/

# Build the config server application
WORKDIR /app
COPY infra/config-server ./infra/config-server
RUN --mount=type=cache,target=/root/.m2 mvn -f infra/config-server/pom.xml package -DskipTests

# Stage 2: Create image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

EXPOSE 8888

COPY --from=builder /app/infra/config-server/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]