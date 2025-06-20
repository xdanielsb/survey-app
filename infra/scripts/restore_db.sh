#!/bin/bash

# Parse input
usage() {
  echo "Usage: $0 --file <backup.dump>"
  exit 1
}

FILE=""
while [[ $# -gt 0 ]]; do
  case "$1" in
    --file) FILE="$2"; shift 2 ;;
    *) usage ;;
  esac
done

[ -z "$FILE" ] && usage
[ ! -f "$FILE" ] && echo "❌ Backup file not found: $FILE" && exit 1

echo "📦 Selected backup: $FILE"

echo "Stopping running services"
docker compose -f infra/docker-compose.yml down

echo "Starting the database only"
docker compose -f infra/docker-compose.yml up db -d

echo "Restoring database using PatronX"
docker compose -f infra/docker-compose.yml run --rm \
  -v "$(realpath "$FILE"):/tmp/backup.dump" \
  --entrypoint patronx patronx-worker \
  restore --inp "/tmp/backup.dump"

echo "Restarting all services"
docker compose -f infra/docker-compose.yml up -d

echo "✅ Restore complete."

