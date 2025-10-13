FROM node:24.10.0-alpine AS builder

WORKDIR /app
COPY . .

ARG BASE_HREF=/backoffice/
ARG KEYCLOAK_URL
ENV KEYCLOAK_URL=$KEYCLOAK_URL

RUN npm install
RUN npm run build -- --base-href=$BASE_HREF

# Use same compatible Node version for runtime
FROM node:24.10.0-alpine AS runner

WORKDIR /app
COPY --from=builder /app /app

RUN npm install -g serve

EXPOSE 4200
CMD ["serve", "-s", "dist/backoffice/browser", "-l", "4200"]
