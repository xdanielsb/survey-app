.PHONY: restore dev-up dev-down dev-logs prod-up prod-down prod-logs 

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
	docker compose -f infra/docker-compose.yml --env-file infra/.env up --build --force-recreate -d

prod-down:
	docker compose -f infra/docker-compose.yml down

prod-logs:
	docker compose -f infra/docker-compose.yml logs -f

