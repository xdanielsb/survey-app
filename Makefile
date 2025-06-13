.PHONY: restore dev-up dev-down dev-logs prod-up prod-down prod-logs caddy-reload container-exec prod-ps prod-restart prod-log-service dev-ps keycloak-up keycloak-down keycloak-logs keycloak-log-service keycloak-ps keycloak-restart ollama-pull

# Usage: make restore FILE=backups/survey-20250601-101329.sql.gz
restore:
	@echo "📦 Restoring from: $(FILE)"
	@./infra/scripts/restore_db.sh --file $(FILE)

prod-up:
	docker compose -f infra/docker-compose.yml --env-file infra/.env up -d

prod-down:
	docker compose -f infra/docker-compose.yml down

prod-logs:
	docker compose -f infra/docker-compose.yml logs -f

prod-log-service:
	docker compose -f infra/docker-compose.yml logs -f $(SERVICE)

prod-ps:
	docker compose -f infra/docker-compose.yml ps

prod-restart:
	docker compose -f infra/docker-compose.yml restart $(SERVICE)

caddy-reload: ## Usage: make caddy-reload CONTAINER=<container-name>
	docker exec $(CONTAINER) caddy reload --config /etc/caddy/Caddyfile --adapter caddyfile

container-logs:
	docker logs -f $(CONTAINER)

container-exec:
	docker exec -it $(CONTAINER) $(CMD)

dev-up:
	docker compose -f infra/docker-compose.dev.yml --env-file infra/.env up --build --force-recreate -d

dev-down:
	docker compose -f infra/docker-compose.dev.yml down

dev-logs:
	docker compose -f infra/docker-compose.dev.yml logs -f

dev-ps:
	docker compose -f infra/docker-compose.dev.yml ps

keycloak-up:
	docker compose -f infra/docker-compose.keycloak.yml --env-file infra/.env up -d

keycloak-down:
	docker compose -f infra/docker-compose.keycloak.yml down

keycloak-logs:
	docker compose -f infra/docker-compose.keycloak.yml logs -f

keycloak-log-service:
	docker compose -f infra/docker-compose.keycloak.yml logs -f $(SERVICE)

keycloak-ps:
	docker compose -f infra/docker-compose.keycloak.yml ps

keycloak-restart:
	docker compose -f infra/docker-compose.keycloak.yml restart $(SERVICE)

ollama-pull:
	docker exec -it ollama ollama pull tinyllama
