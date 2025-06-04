# Survey 
_Cloud‑native survey engine with end‑to‑end observability._

![Backend CI](https://github.com/xdanielsb/survey-app/actions/workflows/ci-backend.yml/badge.svg)
![Frontend CI](https://github.com/xdanielsb/survey-app/actions/workflows/ci-frontend.yml/badge.svg)
[![Frontend Coverage](https://img.shields.io/codecov/c/github/xdanielsb/survey-app?flag=frontend&label=frontend%20coverage&branch=master)](https://app.codecov.io/gh/xdanielsb/survey-app/flags/frontend)
[![Backend Coverage](https://img.shields.io/codecov/c/github/xdanielsb/survey-app?flag=backend&label=backend%20coverage&branch=master)](https://app.codecov.io/gh/xdanielsb/survey-app/flags/backend)
![License](https://img.shields.io/github/license/xdanielsb/survey-app)
![Release](https://img.shields.io/github/v/tag/xdanielsb/survey-app)

This repository contains a full-stack **Survey App** built with:

-  **Spring Boot** (Java 17) for the backend API  
-  **Vue 3 + TypeScript** for the frontend UI  
-  **Security** | Google Sign-in, Firebase Auth ⤳ JWT gateway, backend routes secured
-  **Seamless CI / CD** | Independent pipelines for UI & API, auto‑promote from staging to prod 
-  **PostgreSQL as the database** with flyway to version the database
-  **Database restore** one liner `make restore FILE=backups/{file}`
-  **Docker for local development** 
-  **Periodic rotating backups** Till 30 days
-  **Instrumentation**| Sentry frontend & backend (error tracking)
-  **Payment**| Stripe
-  **Pagination**
-  **Testing** | Vitest + JaCoCo + Codecov + TestContainers
-  **Observability:** Logstash + Elasticsearch + Kibana + Grafana  
-  **Monitoring** | Prometheus + Grafana (Latency, Error Rate, Traffic, Saturation) 
-  **Stress tests with k6** (nightly via GitHub Actions)
-  **Delivery:** GitHub Actions · Docker · Caddy reverse‑proxy
-  **Security scanning** | OWASP Dependency Check and npm audit via GitHub Actions

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


## License
BSD 3-Clause
