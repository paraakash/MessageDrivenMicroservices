#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

echo "Starting RabbitMQ..."
docker compose up -d rabbitmq

echo "Starting reader-service..."
nohup mvn spring-boot:run -pl reader-service -am > "$ROOT_DIR/reader-service.log" 2>&1 &

echo "Starting processor1-service..."
nohup mvn spring-boot:run -pl processor1-service -am > "$ROOT_DIR/processor1-service.log" 2>&1 &

echo "Starting processor2-service..."
nohup mvn spring-boot:run -pl processor2-service -am > "$ROOT_DIR/processor2-service.log" 2>&1 &

echo "Services started."
echo "RabbitMQ UI: http://localhost:15672"
echo "Reader API: http://localhost:8081"
echo "Processor 1: http://localhost:8082"
echo "Processor 2: http://localhost:8083"
