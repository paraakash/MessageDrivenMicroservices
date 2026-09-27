# Message Driven Microservice

This workspace contains three Spring Boot microservices connected through RabbitMQ:

- Reader Service reads CSV files and publishes batches to Q1
- Processor 1 consumes Q1, processes records in parallel, and publishes results to Q2
- Processor 2 consumes Q2, writes result files, and exposes completion statistics via HTTP

## Production-ready configuration highlights

- Durable queues and exchanges
- Listener concurrency controlled by configuration
- Thread pool tuning for record-level parallel processing
- RabbitMQ health checks in Docker Compose
- explicit queue names centralized in a shared config class
- simple web dashboard and JSON stats endpoint for monitoring
- Spring profiles for dev/prod behavior, actuator health endpoints, and environment variable overrides

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker Desktop or Docker Engine

## Start RabbitMQ

```bash
docker compose up -d
```

## Build all services

```bash
mvn clean install
```

## Run services locally

Option 1: start all services with the provided scripts.

Windows PowerShell:

```powershell
./start-services.ps1
```

Linux/macOS:

```bash
chmod +x start-services.sh
./start-services.sh
```

Option 2: open three terminals and run:

```bash
mvn spring-boot:run -pl reader-service -am
mvn spring-boot:run -pl processor1-service -am
mvn spring-boot:run -pl processor2-service -am
```

## Important configuration values

Reader service:

```yaml
reader:
  input-directory: data/input
  output-directory: data/output
  batch-size: 10
  poll-interval-ms: 5000
```

Processor 1:

```yaml
processor1:
  listeners: 3
  thread-pool-core-size: 8
  thread-pool-max-size: 16
```

Processor 2:

```yaml
processor2:
  output-directory: data/output
  listeners: 2
```

## Trigger manual processing

```bash
curl -X POST http://localhost:8081/reader/trigger
```

## Monitoring

- RabbitMQ UI: http://localhost:15672
- Processor 2 dashboard: http://localhost:8083/
- Processor 2 stats JSON: http://localhost:8083/stats

Default RabbitMQ credentials:

```text
username: guest
password: guest
```
