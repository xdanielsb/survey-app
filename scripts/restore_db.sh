#!/usr/bin/env bash
set -euo pipefail

# Load secrets from .env if it exists
if [ -f .env ]; then
  set -a
  grep -E '^[A-Za-z_][A-Za-z0-9_]*=.*' .env > /tmp/env.filtered
  . /tmp/env.filtered
  set +a
fi

# Validate required vars
: "${POSTGRES_DB:?Missing POSTGRES_DB}"
: "${POSTGRES_USER:?Missing POSTGRES_USER}"
: "${POSTGRES_PASSWORD:?Missing POSTGRES_PASSWORD}"

# Parse input
usage() {
  echo "Usage: $0 --file <filename.sql.gz>"
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

# Secure temp SQL file
TMP_SQL="$(mktemp /tmp/restore.XXXXXX.sql)"
chmod 600 "$TMP_SQL"

echo "📂 Decompressing..."
gunzip -c "$FILE" > "$TMP_SQL"

if [ ! -s "$TMP_SQL" ]; then
  echo "❌ Decompression failed or file is empty."
  rm -f "$TMP_SQL"
  exit 1
fi

echo "Down the services to avoid new updates"
docker compose -f infrastructure/docker-compose.yml down
echo "Create container to apply restore"
docker compose -f infrastructure/docker-compose.yml up db -d

echo "Restoring to PostgreSQL..."
# Copy SQL file into the running db container
docker compose -f infrastructure/docker-compose.yml cp "$TMP_SQL" db:/tmp/restore.sql

echo "Dropping and recreating database $POSTGRES_DB"
docker compose -f infrastructure/docker-compose.yml exec db psql -U $POSTGRES_USER -d postgres -c "DROP DATABASE IF EXISTS $POSTGRES_DB;"
docker compose -f infrastructure/docker-compose.yml exec db psql -U $POSTGRES_USER -d postgres -c "CREATE DATABASE $POSTGRES_DB;"
docker compose -f infrastructure/docker-compose.yml exec db bash -c "PGPASSWORD='${POSTGRES_PASSWORD}' psql -U ${POSTGRES_USER} -d ${POSTGRES_DB} < /tmp/restore.sql"

echo "Cleaning up..."
rm -f "$TMP_SQL"

echo "Restoring other services"
docker compose -f infrastructure/docker-compose.yml up -d

echo "✅ Restore complete."
