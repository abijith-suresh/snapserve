#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BASE_URL="${BASE_URL:-http://localhost:9090}"
COMPOSE_FILE="${COMPOSE_FILE:-$ROOT_DIR/docker-compose.yml}"
COMPOSE_PROJECT_NAME="${COMPOSE_PROJECT_NAME:-snapserve-smoke}"
DOCKER_COMPOSE=(docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE")

if [[ -z "${JWT_SECRET:-}" ]]; then
  JWT_SECRET="$(python3 - <<'PY'
import secrets
print(secrets.token_urlsafe(64))
PY
)"
  export JWT_SECRET
fi

if [[ ${#JWT_SECRET} -lt 64 ]]; then
  printf 'JWT_SECRET must be at least 64 characters for smoke checks\n' >&2
  exit 1
fi

export MONGODB_URI="${MONGODB_URI:-}"
export GMAIL_USERNAME="${GMAIL_USERNAME:-}"
export GMAIL_APP_PASSWORD="${GMAIL_APP_PASSWORD:-}"

cleanup() {
  if [[ "${KEEP_STACK:-0}" != "1" ]]; then
    "${DOCKER_COMPOSE[@]}" down -v --remove-orphans >/dev/null 2>&1 || true
  fi
}

wait_for_url() {
  local url="$1"
  local label="$2"

  for _ in $(seq 1 90); do
    if curl --fail --silent "$url" >/dev/null; then
      printf 'ready: %s\n' "$label"
      return 0
    fi
    sleep 2
  done

  printf 'timeout waiting for %s (%s)\n' "$label" "$url" >&2
  return 1
}

print_compose_logs() {
  printf '\ncompose ps\n'
  "${DOCKER_COMPOSE[@]}" ps || true
  printf '\ncompose logs\n'
  "${DOCKER_COMPOSE[@]}" logs --tail=200 || true
}

trap cleanup EXIT

cd "$ROOT_DIR"

./gradlew build

"${DOCKER_COMPOSE[@]}" down -v --remove-orphans >/dev/null 2>&1 || true

"${DOCKER_COMPOSE[@]}" up --build -d

wait_for_url "$BASE_URL/actuator/health" "api-gateway health"
wait_for_url "http://localhost:9000/actuator/health" "auth-service health"
wait_for_url "http://localhost:9001/actuator/health" "user-service health"
wait_for_url "http://localhost:9002/actuator/health" "booking-service health"
wait_for_url "http://localhost:9003/actuator/health" "notification-service health"

register_payload='{"email":"smoke.customer@example.com","password":"Passw0rd!","role":"CUSTOMER"}'
register_response="$(curl --silent --show-error --write-out '\n%{http_code}' \
  -H 'Content-Type: application/json' \
  -d "$register_payload" \
  "$BASE_URL/api/v1/auth/register")"
register_status="$(printf '%s' "$register_response" | python3 -c 'import sys; text = sys.stdin.read().rstrip("\n"); print(text.splitlines()[-1] if text else "")')"

if [[ "$register_status" != "201" && "$register_status" != "409" ]]; then
  print_compose_logs
  printf 'unexpected register status: %s\n' "$register_status" >&2
  exit 1
fi

login_payload='{"email":"smoke.customer@example.com","password":"Passw0rd!"}'
login_response="$(curl --silent --show-error --fail \
  -H 'Content-Type: application/json' \
  -H 'X-Device-Id: smoke-device' \
  -H 'X-Real-IP: 127.0.0.1' \
  -d "$login_payload" \
  "$BASE_URL/api/v1/auth/login")"

access_token="$(printf '%s' "$login_response" | python3 -c 'import json,sys; print(json.load(sys.stdin)["data"]["accessToken"])')"

specialist_register_payload='{"email":"smoke.specialist@example.com","password":"Passw0rd!","role":"SPECIALIST"}'
specialist_register_response="$(curl --silent --show-error --write-out '\n%{http_code}' \
  -H 'Content-Type: application/json' \
  -d "$specialist_register_payload" \
  "$BASE_URL/api/v1/auth/register")"
specialist_register_status="$(printf '%s' "$specialist_register_response" | python3 -c 'import sys; text = sys.stdin.read().rstrip("\n"); print(text.splitlines()[-1] if text else "")')"

if [[ "$specialist_register_status" != "201" && "$specialist_register_status" != "409" ]]; then
  print_compose_logs
  printf 'unexpected specialist register status: %s\n' "$specialist_register_status" >&2
  exit 1
fi

specialist_login_payload='{"email":"smoke.specialist@example.com","password":"Passw0rd!"}'
specialist_login_response="$(curl --silent --show-error --fail \
  -H 'Content-Type: application/json' \
  -H 'X-Device-Id: smoke-specialist-device' \
  -H 'X-Real-IP: 127.0.0.1' \
  -d "$specialist_login_payload" \
  "$BASE_URL/api/v1/auth/login")"

specialist_access_token="$(printf '%s' "$specialist_login_response" | python3 -c 'import json,sys; print(json.load(sys.stdin)["data"]["accessToken"])')"

customer_payload='{"email":"smoke.customer@example.com","name":"Smoke Customer","phone":"+15555550100","address":"123 Smoke Test Lane","preferredPaymentMethod":"PAYPAL"}'
customer_create_response="$(curl --silent --show-error --write-out '\n%{http_code}' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $access_token" \
  -d "$customer_payload" \
  "$BASE_URL/api/v1/customers")"
customer_create_status="$(printf '%s' "$customer_create_response" | python3 -c 'import sys; text = sys.stdin.read().rstrip("\n"); print(text.splitlines()[-1] if text else "")')"

if [[ "$customer_create_status" != "201" && "$customer_create_status" != "409" ]]; then
  print_compose_logs
  printf 'unexpected customer create status: %s\n' "$customer_create_status" >&2
  exit 1
fi

customer_lookup="$(curl --silent --show-error --fail \
  -H "Authorization: Bearer $access_token" \
  "$BASE_URL/api/v1/customers/search?email=smoke.customer@example.com")"
customer_id="$(printf '%s' "$customer_lookup" | python3 -c 'import json,sys; print(json.load(sys.stdin)["data"]["id"])')"

specialist_payload='{"email":"smoke.specialist@example.com","name":"Smoke Specialist","phone":"+15555550101","title":"Master Plumber","services":["PLUMBING"],"hourlyRate":99.50}'
specialist_create_response="$(curl --silent --show-error --write-out '\n%{http_code}' \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $specialist_access_token" \
  -d "$specialist_payload" \
  "$BASE_URL/api/v1/specialists")"
specialist_create_status="$(printf '%s' "$specialist_create_response" | python3 -c 'import sys; text = sys.stdin.read().rstrip("\n"); print(text.splitlines()[-1] if text else "")')"

if [[ "$specialist_create_status" != "201" && "$specialist_create_status" != "409" ]]; then
  print_compose_logs
  printf 'unexpected specialist create status: %s\n' "$specialist_create_status" >&2
  exit 1
fi

specialist_lookup="$(curl --silent --show-error --fail \
  -H "Authorization: Bearer $specialist_access_token" \
  "$BASE_URL/api/v1/specialists/by-service/PLUMBING")"
specialist_id="$(printf '%s' "$specialist_lookup" | python3 -c 'import json,sys; data=json.load(sys.stdin)["data"]; print(next(item["id"] for item in data if item["email"]=="smoke.specialist@example.com"))')"

booking_datetime="$(date -u -d '+2 day' '+%Y-%m-%dT%H:%M:%S')"
booking_payload="$(SPECIALIST_ID="$specialist_id" BOOKING_DATETIME="$booking_datetime" python3 - <<'PY'
import json
import os
print(json.dumps({
    "customerId": "spoofed-customer-id",
    "specialistId": os.environ["SPECIALIST_ID"],
    "bookingDate": os.environ["BOOKING_DATETIME"],
    "notes": "Smoke test booking",
    "price": 149.99,
    "serviceType": "PLUMBING"
}))
PY
)"

booking_response="$(curl --silent --show-error --fail \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $access_token" \
  -d "$booking_payload" \
  "$BASE_URL/api/v1/bookings/")"
booking_id="$(printf '%s' "$booking_response" | python3 -c 'import json,sys; print(json.load(sys.stdin)["data"]["id"])')"
booking_customer_id="$(printf '%s' "$booking_response" | python3 -c 'import json,sys; print(json.load(sys.stdin)["data"]["customerId"])')"

if [[ "$booking_customer_id" != "$customer_id" ]]; then
  print_compose_logs
  printf 'booking customer mismatch: expected %s got %s\n' "$customer_id" "$booking_customer_id" >&2
  exit 1
fi

curl --silent --show-error --fail \
  -H "Authorization: Bearer $access_token" \
  "$BASE_URL/api/v1/bookings/customer/$customer_id" >/dev/null

curl --silent --show-error --fail \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $access_token" \
  -d '{"status":"CANCELLED"}' \
  "$BASE_URL/api/v1/bookings/$booking_id/cancel" >/dev/null

curl --silent --show-error --fail \
  -H "Authorization: Bearer $specialist_access_token" \
  "$BASE_URL/api/v1/notifications/templates" >/dev/null

printf 'backend smoke checks passed\n'
