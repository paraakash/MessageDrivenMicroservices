$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

Write-Host "Starting RabbitMQ..."
docker compose up -d rabbitmq

Write-Host "Starting reader-service..."
$reader = Start-Process -FilePath "powershell" -ArgumentList "-NoExit","-Command","Set-Location '$root'; mvn spring-boot:run -pl reader-service -am" -WorkingDirectory $root -PassThru

Write-Host "Starting processor1-service..."
$processor1 = Start-Process -FilePath "powershell" -ArgumentList "-NoExit","-Command","Set-Location '$root'; mvn spring-boot:run -pl processor1-service -am" -WorkingDirectory $root -PassThru

Write-Host "Starting processor2-service..."
$processor2 = Start-Process -FilePath "powershell" -ArgumentList "-NoExit","-Command","Set-Location '$root'; mvn spring-boot:run -pl processor2-service -am" -WorkingDirectory $root -PassThru

Write-Host "Services started."
Write-Host "RabbitMQ UI: http://localhost:15672"
Write-Host "Reader API: http://localhost:8081"
Write-Host "Processor 1: http://localhost:8082"
Write-Host "Processor 2: http://localhost:8083"
