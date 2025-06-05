# 🧠 Survey App - Backend

This is the backend API for the Survey application, built with **Spring Boot** and **PostgreSQL**.

## 🚀 Features

- Expose surveys via `/surveys/:id`
- Submit anonymous responses
- Retrieve aggregated survey results at `/surveys/:id/results`
- Auto-creates 3 default surveys on application startup
- PostgreSQL persistence using Spring Data JPA

## Technologies

- Java 17
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Maven
- Spotless (code formatting)

# Backups
Backup script that runs daily at 3 AM. Backups are saved 31 days in the past.
```sh
docker compose exec db-backup /backup.sh
```

## Running Tests

```bash
./mvnw clean verify
```

## Run the CLI
```bash
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=shell
    ## write help to see available commands
    ## or in the server
    java -jar app.jar --spring.profiles.active=shell
```

##  Format the code
```
./mvnw spotless:apply
```