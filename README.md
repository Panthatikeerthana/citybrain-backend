# CityBrain Backend (Spring Boot)

AI-driven Urban Infrastructure and Operations Management Engine for Smart Cities.

## Tech Stack
- **Java 17**
- **Spring Boot 4.1.1**
- **Spring Data JPA**
- **Spring Web MVC**
- **MySQL Database**
- **Maven Wrapper**

## Configuration (`application.properties`)
```properties
spring.application.name=citybrain-backend
spring.datasource.url=jdbc:mysql://localhost:3306/citybrain
spring.datasource.username=root
spring.datasource.password=keerthana
server.port=8080
```

## REST API Endpoints
- `GET /api/traffic` - Fetch real-time traffic sensor telemetry
- `GET /api/weather` - Fetch meteorological telemetry and sensor readings

## Running Locally
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```
The server will start at `http://localhost:8080`.
