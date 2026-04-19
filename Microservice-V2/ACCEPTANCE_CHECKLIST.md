# Microservice-V2 Acceptance Checklist

This document is the repeatable manual acceptance checklist for the local Docker Compose stack in this repository.

Run everything from:

```bash
cd /Users/truongthien/Documents/1.Data/3.Programming/11.Kafka_Redis/Microservice-V2
```

## Scope

This checklist verifies:

- Docker Compose can bring up the full local stack
- Database seed data is loaded from `mysql-init/init.sql`
- Order API works
- Kafka flow `Order -> Product` reduces stock
- Kafka flow `Product -> Order` changes order status to `PREPARED`
- Promotion flow with `SUMMER20` works and decrements `usage_limit`
- Redis lock key can be observed during product locking

## Important Notes

- The database source of truth is [`mysql-init/init.sql`](/Users/truongthien/Documents/1.Data/3.Programming/11.Kafka_Redis/Microservice-V2/mysql-init/init.sql).
- This checklist uses the current seed data:
  - Products: `PROD-1001`, `PROD-1002`
  - Promotion code: `SUMMER20`
- `SUMMER20` is seeded as valid from `2026-01-01 00:00:00` to `2026-12-31 23:59:59`.
- If you run this checklist after `2026-12-31`, `SUMMER20` will no longer be valid unless the seed data is updated.
- The direct Order Service endpoint is `http://localhost:8098/api/v1/orders`.
- The Gateway endpoint is `http://localhost:8282/orders/api/v1/orders`.

## Recommended Terminal Layout

Open four terminals.

Terminal 1: infrastructure and app logs

```bash
cd /Users/truongthien/Documents/1.Data/3.Programming/11.Kafka_Redis/Microservice-V2
docker compose logs -f order-service product-service promotion-service api-gateway
```

Terminal 2: Redis monitor

```bash
cd /Users/truongthien/Documents/1.Data/3.Programming/11.Kafka_Redis/Microservice-V2
docker compose exec -T redis redis-cli MONITOR
```

Terminal 3: MySQL helper setup

```bash
cd /Users/truongthien/Documents/1.Data/3.Programming/11.Kafka_Redis/Microservice-V2
export MYSQL_ROOT_PASSWORD=$(grep '^MYSQL_ROOT_PASSWORD=' .env | cut -d= -f2-)
```

Terminal 4: API calls with `curl`

## Step 0: Reset And Start Clean

Run:

```bash
docker compose down -v
docker compose up -d --build
docker compose ps
```

PASS criteria:

- `mysql`, `redis`, `kafka`, `service-registry`, `promotion-service`, `product-service`, `order-service`, `api-gateway` are `Up`
- core services are `healthy`

Useful check:

```bash
docker compose ps
```

## Step 1: Confirm Seed Data From `mysql-init`

Run:

```bash
docker compose exec -T -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql \
  mysql -uroot -e "
SELECT id, name, stock
FROM product_service.products
WHERE id IN ('PROD-1001','PROD-1002')
ORDER BY id;

SELECT code, start_date, end_date, usage_limit
FROM promotion.promotions
WHERE code = 'SUMMER20';
"
```

Expected result:

- `PROD-1001` exists
- `PROD-1002` exists
- `SUMMER20` exists
- `SUMMER20` validity covers the test date if testing during 2026

PASS criteria:

- seed data matches `mysql-init/init.sql`

## Step 2: Capture Baseline Before API Test

Run:

```bash
docker compose exec -T -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql \
  mysql -uroot -e "
SELECT id, stock
FROM product_service.products
WHERE id IN ('PROD-1001','PROD-1002')
ORDER BY id;

SELECT code, usage_limit
FROM promotion.promotions
WHERE code = 'SUMMER20';

SELECT id, customer_id, status, total_amount, promotion_code
FROM order_service.orders
ORDER BY created_date DESC
LIMIT 10;
"
```

Record these values:

- stock of `PROD-1001`
- stock of `PROD-1002`
- `SUMMER20.usage_limit`

## Step 3: Test Create Order Without Promotion

Run:

```bash
curl -i -sS -X POST http://localhost:8098/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": "ACPT-NO-PROMO-001",
    "orderItems": [
      { "productId": "PROD-1001", "quantity": 1 },
      { "productId": "PROD-1002", "quantity": 2 }
    ]
  }'
```

Expected result:

- HTTP `200`
- response contains a new order `id`
- order is created successfully

Query the created order:

```bash
docker compose exec -T -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql \
  mysql -uroot -e "
SELECT id, customer_id, status, total_amount, promotion_code
FROM order_service.orders
WHERE customer_id = 'ACPT-NO-PROMO-001'
ORDER BY created_date DESC
LIMIT 1;
"
```

PASS criteria:

- one new row exists for `ACPT-NO-PROMO-001`

## Step 4: Verify Kafka Flow `Order -> Product`

Observe Terminal 1 and look for these messages:

- `Publish new order success`
- `Receive order message`
- `Updated stock for products`

Check stock in DB:

```bash
docker compose exec -T -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql \
  mysql -uroot -e "
SELECT id, stock
FROM product_service.products
WHERE id IN ('PROD-1001','PROD-1002')
ORDER BY id;
"
```

Expected result after Step 3:

- `PROD-1001` stock decreases by `1`
- `PROD-1002` stock decreases by `2`

PASS criteria:

- Kafka messages are visible in logs
- DB stock matches the order quantities

## Step 5: Verify Kafka Flow `Product -> Order`

Observe Terminal 1 and look for these messages:

- `Locked product success`
- `Receive product`
- `Success to lock product item`

Check order status:

```bash
docker compose exec -T -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql \
  mysql -uroot -e "
SELECT id, customer_id, status
FROM order_service.orders
WHERE customer_id = 'ACPT-NO-PROMO-001'
ORDER BY created_date DESC
LIMIT 1;
"
```

Expected result:

- `status = PREPARED`

PASS criteria:

- order status becomes `PREPARED`

## Step 6: Test Create Order With Promotion `SUMMER20`

Capture promotion baseline immediately before this test:

```bash
docker compose exec -T -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql \
  mysql -uroot -e "
SELECT code, usage_limit, start_date, end_date
FROM promotion.promotions
WHERE code = 'SUMMER20';
"
```

Run:

```bash
curl -i -sS -X POST http://localhost:8098/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": "ACPT-PROMO-001",
    "promotionCode": "SUMMER20",
    "orderItems": [
      { "productId": "PROD-1001", "quantity": 1 },
      { "productId": "PROD-1002", "quantity": 1 }
    ]
  }'
```

Query the new order:

```bash
docker compose exec -T -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql \
  mysql -uroot -e "
SELECT id, customer_id, status, total_amount, promotion_code
FROM order_service.orders
WHERE customer_id = 'ACPT-PROMO-001'
ORDER BY created_date DESC
LIMIT 1;
"
```

PASS criteria:

- HTTP `200`
- order row exists
- status eventually becomes `PREPARED`

## Step 7: Verify Promotion `usage_limit` Decrements By 1

Run:

```bash
docker compose exec -T -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql \
  mysql -uroot -e "
SELECT code, usage_limit, start_date, end_date
FROM promotion.promotions
WHERE code = 'SUMMER20';
"
```

Expected result:

- `usage_limit_after = usage_limit_before - 1`

PASS criteria:

- `SUMMER20` usage limit decreases exactly by `1`

## Step 8: Verify Redis Lock Key

Keep Terminal 2 running:

```bash
docker compose exec -T redis redis-cli MONITOR
```

Trigger another order:

```bash
curl -i -sS -X POST http://localhost:8098/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": "ACPT-REDIS-001",
    "orderItems": [
      { "productId": "PROD-1001", "quantity": 1 },
      { "productId": "PROD-1002", "quantity": 1 }
    ]
  }'
```

Expected Redis key shape:

```text
lock:products:PROD-1001,PROD-1002
```

PASS criteria:

- Terminal 2 shows a command referencing `lock:products:PROD-1001,PROD-1002`

Notes:

- The key is short-lived, so `MONITOR` is more reliable than `SCAN`
- RedisInsight at `http://localhost:8001` can still be used for visual confirmation
- If using RedisInsight manually, connect with:
  - Host: `redis`
  - Port: `6379`

## Step 9: Final Database Summary

Run:

```bash
docker compose exec -T -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql \
  mysql -uroot -e "
SELECT id, stock
FROM product_service.products
WHERE id IN ('PROD-1001','PROD-1002')
ORDER BY id;

SELECT id, customer_id, status, total_amount, promotion_code
FROM order_service.orders
WHERE customer_id IN ('ACPT-NO-PROMO-001','ACPT-PROMO-001','ACPT-REDIS-001')
ORDER BY created_date DESC;

SELECT code, usage_limit
FROM promotion.promotions
WHERE code = 'SUMMER20';
"
```

PASS criteria:

- stock reflects all successful test orders
- created acceptance orders exist
- promotion usage reflects one successful promotion use

## Optional: Run The Existing Automated Smoke Test

This repository already includes a quick smoke test:

```bash
./scripts/verify.sh
```

Use it as a fast pre-check before the full manual acceptance flow.

## Automated Acceptance Runner

If you want one command that:

- checks whether the application images already exist
- skips rebuild when the stack is already ready
- starts the stack without rebuild when images exist but containers are not fully up
- rebuilds only when required application images are missing
- runs the automated verification at the end

use:

```bash
./scripts/acceptance-test.sh
```

Notes:

- if the current stack is already healthy, this script skips both rebuild and restart
- if images exist but the stack is stopped or partially unhealthy, it runs `docker compose up -d`
- if one of the app images is missing, it runs `docker compose up -d --build`
- after the stack is ready, it calls `./scripts/verify.sh`

## Optional: Test Through Gateway

If you also want to verify the Gateway route, run:

```bash
curl -i -sS -X POST http://localhost:8282/orders/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": "ACPT-GW-001",
    "promotionCode": "SUMMER20",
    "orderItems": [
      { "productId": "PROD-1001", "quantity": 1 },
      { "productId": "PROD-1002", "quantity": 1 }
    ]
  }'
```

## PASS/FAIL Report Template

Copy this template into your notes after each run:

```md
# Bien Ban Nghiem Thu Microservice-V2

Ngay test:
Nguoi test:
Branch/commit:

## Tong quan
- TC01 Docker Compose stack: PASS/FAIL
- TC02 DB seed from mysql-init: PASS/FAIL
- TC03 Create order without promotion: PASS/FAIL
- TC04 Kafka Order -> Product stock update: PASS/FAIL
- TC05 Kafka Product -> Order status PREPARED: PASS/FAIL
- TC06 Create order with SUMMER20: PASS/FAIL
- TC07 Promotion usage_limit decrement: PASS/FAIL
- TC08 Redis lock observation: PASS/FAIL

## Chi tiet

| Test case | Command | Expected | Actual | Result |
|---|---|---|---|---|
| TC01 | `docker compose ps` | required services Up/healthy |  | PASS/FAIL |
| TC02 | MySQL seed query | products and SUMMER20 exist |  | PASS/FAIL |
| TC03 | POST direct OrderService | HTTP 200, order created |  | PASS/FAIL |
| TC04 | logs + product query | stock decreases correctly |  | PASS/FAIL |
| TC05 | order query | status = PREPARED |  | PASS/FAIL |
| TC06 | POST with SUMMER20 | HTTP 200, order created |  | PASS/FAIL |
| TC07 | promotion query | usage_limit - 1 |  | PASS/FAIL |
| TC08 | Redis MONITOR | lock key observed |  | PASS/FAIL |

## Evidence
- docker compose ps:
- curl response no promotion:
- curl response with promotion:
- order-service logs:
- product-service logs:
- promotion-service logs:
- products before/after:
- orders after tests:
- promotions after tests:
- redis monitor output:

## Ket luan
- Nghiem thu chung: PASS/FAIL
- Ghi chu:
```
