# Survey 
_Cloud‑native survey engine with end‑to‑end observability._

![Backend CI](https://github.com/xdanielsb/survey-app/actions/workflows/ci-backend.yml/badge.svg)
![Frontend CI](https://github.com/xdanielsb/survey-app/actions/workflows/ci-frontend.yml/badge.svg)
![License](https://img.shields.io/github/license/xdanielsb/survey-app)
![Release](https://img.shields.io/github/v/tag/xdanielsb/survey-app)

This repository contains a full-stack **Survey App** built with:

-  **Spring Boot** (Java 17) for the backend API  
-  **Vue 3 + TypeScript** for the frontend UI  
-  **Security** | Firebase Auth ⤳ JWT gateway
-  **Seamless CI / CD** | Independent pipelines for UI & API, auto‑promote from staging to prod 
-  **PostgreSQL as the database** with flyway to version the database 
-  **Docker for local development** 
-  **Periodic rotating backups** Till 30 days
-  **Instrumentation**| Sentry frontend & backend (error tracking)

###  Dev development

```bash
 # db + api + ui + elk
 docker compose -f docker-compose.dev.yml --env-file .env up --build --force-recreate -d

 # backend
 ./mvnw --debug spring-boot:run

 # frontend
 npm run dev
```

More details: [`backend/README.md`](./backend/README.md)
More details: [`frontend/README.md`](./frontend/README.md)


---
##  Stack
* **Backend:** Spring Boot 17 · Postgres · Flyway · Testcontainers  
* **Frontend:** Vue 3 · Vite · TypeScript · Pinia  
* **Observability:** Logstash → Elasticsearch → Kibana · Grafana  
* **Delivery:** GitHub Actions · Docker · Caddy reverse‑proxy  
---

## License
BSD 3-Clause
