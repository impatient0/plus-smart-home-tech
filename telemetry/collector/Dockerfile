# Stage 1: Build application
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Install 'avro-schemas' dependency
COPY pom.xml .
COPY telemetry/pom.xml ./telemetry/
COPY telemetry/serialization/pom.xml ./telemetry/serialization/

RUN mvn install -N

RUN mvn install -N -f telemetry/pom.xml
RUN mvn install -N -f telemetry/serialization/pom.xml

# Package the 'collector' module
COPY telemetry/serialization/avro-schemas ./telemetry/serialization/avro-schemas
RUN mvn install -f telemetry/serialization/avro-schemas/pom.xml -DskipTests

COPY telemetry/collector ./telemetry/collector
RUN mvn package -f telemetry/collector/pom.xml -DskipTests

# Stage 2: Create image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/telemetry/collector/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]