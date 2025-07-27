# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Install parent POMs
COPY pom.xml .
COPY infra/pom.xml ./infra/
COPY infra/config-server/pom.xml ./infra/config-server/

RUN mvn install -N
RUN mvn install -N -f infra/pom.xml
RUN mvn install -N -f infra/config-server/pom.xml

# Build the server config application
COPY infra/config-server ./infra/config-server
RUN mvn -f /app/pom.xml package -DskipTests

# Stage 2: Create image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

EXPOSE 8888

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]