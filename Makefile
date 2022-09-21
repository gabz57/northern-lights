all: build-dev up
.PHONY: all

build-api:
	./gradlew :chat:apps:northernlights-chat-api:docker
build-api-event-publisher:
	./gradlew :chat:apps:northernlights-chat-api-event-publisher:docker
build-dev:
	@make -s build-api
	@make -s build-api-event-publisher

restart-api:
	@docker-compose -p apps -f ./chat/apps/docker-compose.yml rm --force --stop chat-api
	@docker-compose -p apps -f ./chat/apps/docker-compose.yml up -d chat-api

refresh-api:
	@make -s build-api
	@make -s restart-api

up:
	-@docker network create northernlights-net
	@docker-compose -p apps -f ./chat/apps/docker-compose.yml up -d
down:
	@docker-compose -p apps -f ./chat/apps/docker-compose.yml down
