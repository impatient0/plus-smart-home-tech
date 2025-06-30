# --- Stage 1: Build the application in logical, cacheable steps ---
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# --- 1. Install all parent POMs ---
# This creates a base layer with the project's "skeleton".
# This layer will only be rebuilt if a POM file changes.
COPY pom.xml .
COPY telemetry/pom.xml ./telemetry/
COPY telemetry/serialization/pom.xml ./telemetry/serialization/

# The -N flag tells Maven "install this POM, but do not build its modules".
# This is fast and populates the local repo with the parent definitions.
RUN mvn install -N

# Install the telemetry and serialization parents specifically.
RUN mvn install -N -f telemetry/pom.xml
RUN mvn install -N -f telemetry/serialization/pom.xml


# --- 2. Build and install the `avro-schemas` dependency ---
# This layer depends on the parent POMs being installed.
COPY telemetry/serialization/avro-schemas ./telemetry/serialization/avro-schemas
RUN mvn install -f telemetry/serialization/avro-schemas/pom.xml -DskipTests


# --- 3. Finally, build the `collector` application ---
# This layer depends on all the above. It changes most frequently.
COPY telemetry/collector ./telemetry/collector
RUN mvn package -f telemetry/collector/pom.xml -DskipTests


# --- Stage 2: Create the final, lightweight image ---
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy ONLY the final JAR file from the 'builder' stage
COPY --from=builder /app/telemetry/collector/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]