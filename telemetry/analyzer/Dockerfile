# Stage 1: Build application
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Install parent POMs
COPY pom.xml .
COPY telemetry/pom.xml ./telemetry/
COPY telemetry/serialization/pom.xml ./telemetry/serialization/
COPY telemetry/telemetry-commons/pom.xml ./telemetry/telemetry-commons/

RUN mvn install -N
RUN mvn install -N -f telemetry/pom.xml
RUN mvn install -N -f telemetry/serialization/pom.xml

# Install serialization dependencies
COPY telemetry/serialization/avro-schemas ./telemetry/serialization/avro-schemas
RUN mvn install -f telemetry/serialization/avro-schemas/pom.xml -DskipTests

COPY telemetry/serialization/proto-schemas ./telemetry/serialization/proto-schemas
RUN mvn install -f telemetry/serialization/proto-schemas/pom.xml -DskipTests

# Install common dependencies
COPY telemetry/telemetry-commons/pom.xml ./telemetry/telemetry-commons/
RUN mvn install -f telemetry/telemetry-commons/pom.xml -DskipTests

# Build the analyzer application
COPY telemetry/analyzer/pom.xml ./telemetry/analyzer/
RUN mvn -f telemetry/analyzer/pom.xml dependency:go-offline
COPY telemetry/analyzer/src ./telemetry/analyzer/src
RUN mvn package -f telemetry/analyzer/pom.xml -DskipTests

# Stage 2: Create image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/telemetry/analyzer/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]