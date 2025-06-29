.PHONY: prod-restore-db prod-list-backups prod-backup prod-up prod-down prod-logs \
        prod-log-service prod-ps prod-restart \
        caddy-reload container-logs container-exec ollama-pull
# Production database operations
prod-restore-db: ## Restore database from FILE
	@echo "📦 Restoring from: $(FILE)"
	@./infra/compose/scripts/restore_db.sh --file $(FILE)

prod-list-backups: ## List available database backups
	docker compose -f infra/compose/docker-compose.yml run --rm --entrypoint patronx patronx-worker list

prod-backup: ## Create a new database backup
	docker compose -f infra/compose/docker-compose.yml run --rm --entrypoint patronx patronx-worker backup

prod-up: ## Start production stack
	docker compose -f infra/compose/docker-compose.yml --env-file infra/compose/.env up -d

prod-down: ## Stop production stack
	docker compose -f infra/compose/docker-compose.yml down

prod-logs: ## Tail production logs
	docker compose -f infra/compose/docker-compose.yml logs -f

prod-log-service: ## Tail logs for SERVICE in production
	docker compose -f infra/compose/docker-compose.yml logs -f $(SERVICE)

prod-ps: ## Show container status for production
	docker compose -f infra/compose/docker-compose.yml ps

prod-restart: ## Restart SERVICE in production
	docker compose -f infra/compose/docker-compose.yml restart $(SERVICE)

caddy-reload: ## Reload caddy configuration; make caddy-reload CONTAINER=<name>
	docker exec $(CONTAINER) caddy reload --config /etc/caddy/Caddyfile --adapter caddyfile

container-logs: ## Follow logs from a container; make container-logs CONTAINER=<name>
	docker logs -f $(CONTAINER)

container-exec: ## Exec into container; make container-exec CONTAINER=<name> CMD="sh"
	docker exec -it $(CONTAINER) $(CMD)

ollama-pull: ## Pull tinyllama model in ollama container
	docker exec -it ollama ollama pull tinyllama
