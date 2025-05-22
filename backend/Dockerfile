FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar


ARG FIREBASE_API_KEY
ENV FIREBASE_API_KEY=$FIREBASE_API_KEY

ARG LOGSTASH_HOST
ENV LOGSTASH_HOST=$LOGSTASH_HOST

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
