FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY ../../pom.xml .
COPY ../pom.xml ./telemetry/
COPY ./pom.xml ./telemetry/collector/

COPY ./src ./telemetry/collector/src
COPY ../serialization/avro-schemas/src/main/avro ./telemetry/serialization/avro-schemas/src/main/avro

RUN mvn -f ./pom.xml clean package -DskipTests


FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/telemetry/collector/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]