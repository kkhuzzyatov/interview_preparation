#!/usr/bin/env bash

set -e

echo "==> Building application locally..."
./gradlew bootJar --no-daemon

echo "==> Removing old containers..."
docker compose down

echo "==> Building Docker image..."
docker compose build

echo "==> Starting services..."
docker compose up