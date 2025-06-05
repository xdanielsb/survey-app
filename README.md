# Survey 

_Cloud‑native survey engine with end‑to‑end observability._

![Backend CI](https://github.com/xdanielsb/survey-app/actions/workflows/ci-backend.yml/badge.svg)
![Frontend CI](https://github.com/xdanielsb/survey-app/actions/workflows/ci-frontend.yml/badge.svg)
![Analytics CI](https://github.com/xdanielsb/survey-app/actions/workflows/ci-analytics.yml/badge.svg)
[![Frontend Coverage](https://img.shields.io/codecov/c/github/xdanielsb/survey-app?flag=frontend&label=frontend%20coverage&branch=master)](https://app.codecov.io/gh/xdanielsb/survey-app/flags/frontend)
[![Backend Coverage](https://img.shields.io/codecov/c/github/xdanielsb/survey-app?flag=backend&label=backend%20coverage&branch=master)](https://app.codecov.io/gh/xdanielsb/survey-app/flags/backend)
![License](https://img.shields.io/github/license/xdanielsb/survey-app)
![Release](https://img.shields.io/github/v/tag/xdanielsb/survey-app)

This repository contains a full-stack **Survey App** built with:

-  **Security** | Google Sign-in, Firebase Auth, JWT gateway, api routes protected, cookies 
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
-  **Security scanning** | OWASP Dependency Check and npm audit via GitHub Actions
 -  **LLMs for analytics** | analytics project connected to LLM models to provide AI service insights
 -  **Delivery:** GitHub Actions · Docker · Caddy reverse‑proxy

## Project Structure

```
services/
  backend/     (spring boot)
  frontend/    (vuejs)
  analytics/   (flaskapi)
infra/
  monitoring/
  elk/
  docker-compose.yml
  docker-compose.dev.yml
  Caddyfile
  scripts/
performance/stress_tests/
Makefile
```

###  Dev development

```bash
 # db + api + ui + elk

 $ make dev-up           # start all services
 # docker compose -f infra/docker-compose.dev.yml --env-file infra/.env up --build --force-recreate -d

 # stop services
 $ make dev-down

 # tail container logs
 $ make dev-logs

 # /services/backend
 $ ./mvnw --debug spring-boot:run

 # /services/frontend
 $ npm run dev
```

More details: [`backend/README.md`](./services/backend/README.md)
More details: [`frontend/README.md`](./services/frontend/README.md)


## License
BSD 3-Clause
