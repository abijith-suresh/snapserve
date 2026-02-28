# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew :backend:notification-service:bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/backend/notification-service/build/libs/*.jar app.jar
EXPOSE 9003
ENTRYPOINT ["java", "-jar", "app.jar"]
