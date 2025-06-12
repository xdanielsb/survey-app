# Survey 

_Cloud‑native survey engine with end‑to‑end observability._

![Backend CI](https://github.com/xdanielsb/survey-app/actions/workflows/ci-backend.yml/badge.svg)
![Frontend CI](https://github.com/xdanielsb/survey-app/actions/workflows/ci-frontend.yml/badge.svg)
![Analytics CI](https://github.com/xdanielsb/survey-app/actions/workflows/ci-analytics.yml/badge.svg)
![Backoffice CI](https://github.com/xdanielsb/survey-app/actions/workflows/ci-backoffice.yml/badge.svg)
[![Frontend Coverage](https://img.shields.io/codecov/c/github/xdanielsb/survey-app?flag=frontend&label=frontend%20coverage&branch=master)](https://app.codecov.io/gh/xdanielsb/survey-app/flags/frontend)
[![Backend Coverage](https://img.shields.io/codecov/c/github/xdanielsb/survey-app?flag=backend&label=backend%20coverage&branch=master)](https://app.codecov.io/gh/xdanielsb/survey-app/flags/backend)
[![Analytics Coverage](https://img.shields.io/codecov/c/github/xdanielsb/survey-app?flag=analytics&label=analytics%20coverage&branch=master)](https://app.codecov.io/gh/xdanielsb/survey-app/flags/analytics)
[![Backoffice Coverage](https://img.shields.io/codecov/c/github/xdanielsb/survey-app?flag=backoffice&label=backoffice%20coverage&branch=master)](https://app.codecov.io/gh/xdanielsb/survey-app/flags/backoffice)
![License](https://img.shields.io/github/license/xdanielsb/survey-app)
![Release](https://img.shields.io/github/v/tag/xdanielsb/survey-app)

## Key Features

-  **Seamless CI / CD** | Independent pipelines for UI & API, auto‑promote from staging to prod 
-  **Security** | Keycloak, Google Sign-in  JWT gateway, api routes protected, secure cookies
-  **Keycloak** | Multi-realm configuration (production, staging, development)
-  **Security scanning** | OWASP Dependency Check and npm audit via GitHub Actions
-  **RateLimiter** | Bucket4J
-  **Data Layer** PostgreSQL, Flyway to versioned schema migrations
-  **Database restore** one liner `make restore FILE=backups/{file}`
-  **Periodic rotating backups** 30 days retention
-  **Instrumentation**| Sentry frontend & backend (error tracking)
-  **Payment**| Stripe (credit purchase flow)
-  **Emails** | Sendgrid (when buy a survey)
-  **Theme** | Dark/White theme
-  **Observability:** Logstash + Elasticsearch + Kibana + Grafana  
-  **Monitoring** | Prometheus + Grafana (Latency, Error Rate, Traffic, Saturation) 
-  **Unit/Integration Test** | Vitest + JaCoCo + Codecov + TestContainers
-  **Stress tests** with k6 (nightly via GitHub Actions)
-  **LLMs** | analytics project connected to LLM models to provide AI service insights
-  **Delivery** GitHub Actions · Docker · Caddy reverse‑proxy

## Project Structure

```
services/
  backend/     (spring boot)
  frontend/    (vuejs)
  analytics/   (fastapi)
  backoffice/   (angular)
infra/
  monitoring/
  elk/
  scripts/
  keycloak/
  docker-compose.yml
  docker-compose.dev.yml
  Caddyfile
performance/
  stress_tests/
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
More details: [`analytics/README.md`](./services/analytics/README.md)


## License
BSD 3-Clause
