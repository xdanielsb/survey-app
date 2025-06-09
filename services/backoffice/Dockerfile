FROM node:18-alpine AS builder
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

ARG KEYCLOAK_URL
ENV KEYCLOAK_URL=$KEYCLOAK_URL

FROM node:18-alpine AS runner
WORKDIR /app
COPY --from=builder /app /app
RUN npm install -g serve
EXPOSE 4200
CMD ["serve", "-s", "dist/backoffice", "-l", "4200"]
