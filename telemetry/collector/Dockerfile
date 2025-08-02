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

# Build the collector application
COPY telemetry/collector/pom.xml ./telemetry/collector/
RUN mvn -f telemetry/collector/pom.xml dependency:go-offline
COPY telemetry/collector/src ./telemetry/collector/src
RUN mvn package -f telemetry/collector/pom.xml -DskipTests

# Stage 2: Create image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/telemetry/collector/target/*.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]