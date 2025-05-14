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

## Running Tests

```bash
./mvnw clean verify
```

##  Format the code
```
./mvnw spotless:apply
```