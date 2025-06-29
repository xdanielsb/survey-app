.PHONY: monitor-up monitor-down monitor-logs monitor-log-service monitor-ps monitor-restart prometheus-targets

MON_COMPOSE=infra/monitoring/docker-compose.yml

monitor-up: ## Start monitoring stack
	docker compose -f $(MON_COMPOSE) up -d

monitor-down: ## Stop monitoring stack
	docker compose -f $(MON_COMPOSE) down

monitor-logs: ## Tail monitoring logs
	docker compose -f $(MON_COMPOSE) logs -f

monitor-log-service: ## Tail logs for monitoring SERVICE
	docker compose -f $(MON_COMPOSE) logs -f $(SERVICE)

monitor-ps: ## Show container status for monitoring
	docker compose -f $(MON_COMPOSE) ps

monitor-restart: ## Restart monitoring SERVICE
	docker compose -f $(MON_COMPOSE) restart $(SERVICE)

prometheus-targets: ## Show active Prometheus targets
	docker compose -f $(MON_COMPOSE) exec prometheus wget -qO- http://localhost:9090/api/v1/targets?state=active
