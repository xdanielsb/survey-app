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
-  **Periodic backups & Database Recovery** with [PatronX](https://github.com/xdanielsb/patronx)
-  **List backups** `make list-backups` via PatronX
-  **Remote asset store** | Upload backups to S3 using PatronX 
-  **Instrumentation**| Sentry frontend, backend, analytics, sentry (error tracking)
-  **Payment**| Stripe (credit purchase flow)
-  **Invoice PDF** | Invoice generation for payments
-  **Emails** | Sendgrid (when buy a survey)
-  **Theme** | Dark/White theme
-  **Observability:** Logstash + Elasticsearch + Kibana + Grafana  
-  **Monitoring** | Prometheus + Grafana (Latency, Error Rate, Traffic, Saturation) 
-  **Unit/Integration Test** | Vitest + JaCoCo + Codecov + TestContainers
-  **Stress tests** with k6 (nightly via GitHub Actions)
-  **LLMs** | analytics service exposes an AI chat endpoint to talk about the surveys.
-  **Delivery** GitHub Actions · Docker · Caddy reverse‑proxy


## Docs
-  **See** [Database Recovery Guide](docs/data-recovery.md) for how to restore the database in case of an incident
-  **See** [Secure Docker Deploy Guide](docs/secure-deploy.md) for deploying in a safe way with docker compsoe with a dedicated non root user
-  **See** [Deploy Keycloak](docs/deploy-keycloak.md) for deploying keycloak
-  **See** [Monitoring Configuration](docs/monitoring-config.md) for customizing Prometheus targets
## Project Structure

```
services/
  backend/     (spring boot 3 java 17)
  frontend/    (vuejs v3)
  backoffice/  (angular v20)
  analytics/   (fastapi python3.12)
infra/
  scripts/
  keycloak/
    docker-compose.yml
    Caddyfile
  monitoring/
    elk/
    grafana/
    docker-compose.yml
    Caddyfile
  compose/
    docker-compose.yml
    docker-compose.dev.yml
    Caddyfile
docs/
  data-recovery.md
  secure-docker-deploy.md
performance/
  stress_tests/
Makefile
```

###  Dev development
Run `make help` to view all available targets.

```bash
 # db + api + ui + elk

 $ make dev-up           # start all services
 # docker compose -f infra/compose/docker-compose.dev.yml --env-file infra/compose/.env up --build --force-recreate -d

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
More details: [`backoffice/README.md`](./services/backoffice/README.md)


## License
BSD 3-Clause
