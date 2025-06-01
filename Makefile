.PHONY: restore

# Usage: make restore FILE=backups/survey-20250601-101329.sql.gz
restore:
	@echo "📦 Restoring from: $(FILE)"
	@./scripts/restore.sh --file $(FILE)
