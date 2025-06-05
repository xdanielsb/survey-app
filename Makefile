.PHONY: restore dev-up dev-down dev-logs

# Usage: make restore FILE=backups/survey-20250601-101329.sql.gz
restore:
	@echo "📦 Restoring from: $(FILE)"
       @./infra/scripts/restore_db.sh --file $(FILE)

dev-up:
       docker compose -f infra/docker-compose.dev.yml --env-file infra/.env up --build --force-recreate -d

dev-down:
	docker compose down

dev-logs:
	docker compose logs -f
