# Multi-stage production build for Spring Boot Backend on Render
# Stage 1: Build JAR using Maven Wrapper
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Cache Maven dependencies layer
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and package executable JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Minimal, secure runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Run as non-privileged security group and user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy executable Spring Boot JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser

# Render injects the PORT environment variable (defaults to 8080)
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

# Execute Spring Boot application binding to Render PORT
ENTRYPOINT ["sh", "-c", "exec java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=prod -Dserver.port=${PORT:-8080} -jar app.jar"]