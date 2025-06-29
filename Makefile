.PHONY: help

-include makefiles/compose.mk
-include makefiles/dev.mk
-include makefiles/keycloak.mk
-include makefiles/monitoring.mk

help: ## Show this help message
	@grep -h -E '^[a-zA-Z_-]+:.*?##' Makefile makefiles/*.mk | \
	  awk -F':|##' '{printf "  %-20s %s\n", $$1, $$3}' | sort
