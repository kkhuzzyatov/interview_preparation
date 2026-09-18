#!/usr/bin/env bash

set -e

docker exec -it interview-preparation-db \
psql -U postgres -d interview_preparation \
-c "UPDATE public.users SET role = 'ADMIN' WHERE username = 'kkhuzzyatov';"