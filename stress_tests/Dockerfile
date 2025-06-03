FROM grafana/k6:latest

WORKDIR /tests
COPY . .

ENTRYPOINT ["sh", "-c", "k6 run -e BASE_URL=${BASE_URL} $K6_SCRIPT"]
