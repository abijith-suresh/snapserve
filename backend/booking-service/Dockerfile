# Build stage
FROM gradle:9.4.0-jdk21-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle :backend:booking-service:bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget
COPY --from=builder /app/backend/booking-service/build/libs/*.jar app.jar
EXPOSE 9002
ENTRYPOINT ["java", "-jar", "app.jar"]
