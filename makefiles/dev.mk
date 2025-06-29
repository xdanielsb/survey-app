.PHONY: dev-up dev-down dev-logs dev-ps

# Development stack

dev-up: ## Start development stack
	docker compose -f infra/compose/docker-compose.dev.yml --env-file infra/compose/.env up --build --force-recreate -d

dev-down: ## Stop development stack
	docker compose -f infra/compose/docker-compose.dev.yml down

dev-logs: ## Tail development logs
	docker compose -f infra/compose/docker-compose.dev.yml logs -f

dev-ps: ## Show container status for development
	docker compose -f infra/compose/docker-compose.dev.yml ps
