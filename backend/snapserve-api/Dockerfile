FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /workspace

COPY gradle gradle
COPY gradlew gradlew
COPY settings.gradle.kts build.gradle.kts ./
COPY backend/snapserve-api backend/snapserve-api

RUN chmod +x gradlew \
    && ./gradlew :backend:snapserve-api:bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine

RUN apk add --no-cache wget \
    && addgroup -S snapserve \
    && adduser -S snapserve -G snapserve

WORKDIR /app

COPY --from=builder /workspace/backend/snapserve-api/build/libs/*.jar app.jar

USER snapserve

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
