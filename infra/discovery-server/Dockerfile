# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Install parent POMs
COPY pom.xml .
COPY infra/pom.xml ./infra/
COPY infra/discovery-server/pom.xml ./infra/discovery-server/

RUN --mount=type=cache,target=/root/.m2 mvn install -N
RUN --mount=type=cache,target=/root/.m2 mvn install -N -f infra/pom.xml

# Download and cache dependencies
RUN --mount=type=cache,target=/root/.m2 mvn -f infra/discovery-server/pom.xml dependency:go-offline

# Build the discovery server application
COPY infra/discovery-server ./infra/discovery-server
RUN --mount=type=cache,target=/root/.m2 mvn -f infra/discovery-server/pom.xml package -DskipTests

# Stage 2: Create image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

EXPOSE 8761

COPY --from=builder /app/infra/discovery-server/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]