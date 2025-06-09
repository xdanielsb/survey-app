.PHONY: restore dev-up dev-down dev-logs prod-up prod-down prod-logs caddy-reload container-exec prod-ps prod-restart

# Usage: make restore FILE=backups/survey-20250601-101329.sql.gz
restore:
	@echo "📦 Restoring from: $(FILE)"
	@./infra/scripts/restore_db.sh --file $(FILE)

dev-up:
	docker compose -f infra/docker-compose.dev.yml --env-file infra/.env up --build --force-recreate -d

dev-down:
	docker compose -f infra/docker-compose.dev.yml down

dev-logs:
	docker compose -f infra/docker-compose.dev.yml logs -f

prod-up:
	docker compose -f infra/docker-compose.yml --env-file infra/.env up -d

prod-down:
	docker compose -f infra/docker-compose.yml down

prod-logs:
	docker compose -f infra/docker-compose.yml logs -f

caddy-reload: ## Usage: make caddy-reload CONTAINER=<container-name>
	docker exec $(CONTAINER) caddy reload --config /etc/caddy/Caddyfile --adapter caddyfile

container-logs:
	docker logs -f $(CONTAINER)

container-exec:
	docker exec -it $(CONTAINER) $(CMD)

prod-ps:
	docker compose -f infra/docker-compose.yml ps

prod-restart:
	docker compose -f infra/docker-compose.yml restart $(SERVICE)
