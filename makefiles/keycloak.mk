.PHONY: keycloak-up keycloak-down keycloak-logs keycloak-log-service keycloak-ps keycloak-restart

keycloak-up: ## Start Keycloak stack
	docker compose -f infra/keycloak/docker-compose.yml --env-file infra/keycloak/.env up -d

keycloak-down: ## Stop Keycloak stack
	docker compose -f infra/keycloak/docker-compose.yml down

keycloak-logs: ## Tail Keycloak logs
	docker compose -f infra/keycloak/docker-compose.yml logs -f

keycloak-log-service: ## Tail logs for a Keycloak SERVICE
	docker compose -f infra/keycloak/docker-compose.yml logs -f $(SERVICE)

keycloak-ps: ## Show container status for Keycloak
	docker compose -f infra/keycloak/docker-compose.yml ps

keycloak-restart: ## Restart a Keycloak SERVICE
	docker compose -f infra/keycloak/docker-compose.yml restart $(SERVICE)
