# Microservice V2 Local Stack

This repository runs the full local integration stack from the repo root with Docker Compose.

Service module directories in this repo are:

- `APIGateway/`
- `ServiceRegistry/`
- `OrderService/`
- `ProductService/`
- `PromotionService/`

## Included services

- MySQL
- Redis
- RedisInsight
- Zookeeper
- Kafka
- Kafka UI
- Service Registry
- API Gateway
- Promotion Service
- Product Service
- Order Service

## Prerequisites

- Docker Desktop or Docker Engine with Compose support
- `curl` for manual or scripted verification

## Start the stack

Run everything from the repository root:

```bash
cp .env.example .env
# edit .env if you want different local passwords or port mappings

docker compose up -d --build
docker compose ps
```

Optional preflight render:

```bash
docker compose config
```

Useful follow-up commands:

```bash
docker compose logs -f service-registry api-gateway order-service product-service promotion-service
```

To stop the stack:

```bash
docker compose down
```

To reset MySQL and re-run `mysql-init/init.sql`:

```bash
docker compose down -v
docker compose up -d --build
```

Notes:

- `mysql-init/init.sql` is mounted into `/docker-entrypoint-initdb.d` by Compose and runs only when the MySQL volume is created fresh.
- If you need to re-seed MySQL, use `docker compose down -v` before starting again.
- `.env` is intentionally local-only and is ignored by git. Commit `.env.example` instead when defaults change.

## Ports

- API Gateway: `http://localhost:8282`
- Service Registry: `http://localhost:8761`
- Promotion Service: `http://localhost:8082`
- Product Service: `http://localhost:8091`
- Order Service: `http://localhost:8098`
- Kafka UI: `http://localhost:8080`
- RedisInsight: `http://localhost:8001`
- MySQL: `localhost:3306`
- Redis: `localhost:6379`
- Kafka host listener: `localhost:29092`

## Profiles

- Docker Compose runs every Spring service with `SPRING_PROFILES_ACTIVE=docker`
- Local IDE or Maven runs can use `SPRING_PROFILES_ACTIVE=local`
- If no profile is provided, the services default to the local-oriented values defined in the app configs

## Example API checks

Gateway routes keep the current path shape, so the gateway prefixes are:

- Orders: `/orders/**`
- Products: `/products/**`
- Promotions: `/promotions/**`

Create an order without promotion:

```bash
curl -X POST http://localhost:8282/orders/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": "CUST-LOCAL-001",
    "orderItems": [
      { "productId": "PROD-1001", "quantity": 1 },
      { "productId": "PROD-1002", "quantity": 2 }
    ]
  }'
```

Create an order with promotion:

```bash
curl -X POST http://localhost:8282/orders/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": "CUST-LOCAL-002",
    "promotionCode": "SUMMER20",
    "orderItems": [
      { "productId": "PROD-1001", "quantity": 1 },
      { "productId": "PROD-1002", "quantity": 2 }
    ]
  }'
```

Notes:

- The synchronous order-create response is expected to show the order before the Kafka round-trip finishes.
- After Kafka finishes, the order status should move from `NEW` to `PREPARED`.
- `SUMMER20` is seeded as valid from `2026-01-01 00:00:00` through `2026-12-31 23:59:59`, which covers the current local-test date of `2026-04-19`.

## Verification checklist

For a repeatable smoke test, you can run:

```bash
./scripts/verify.sh
```

The script assumes the stack is already running. It checks stack endpoints, creates an order with `SUMMER20`, verifies stock changes, verifies `PREPARED` status, checks `usage_limit` decrement, and tries to observe the Redis lock key.

### 1. Create order

Create a baseline order:

```bash
curl -X POST http://localhost:8282/orders/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": "CUST-VERIFY-001",
    "orderItems": [
      { "productId": "PROD-1001", "quantity": 1 },
      { "productId": "PROD-1002", "quantity": 2 }
    ]
  }'
```

Check the latest orders:

```bash
docker compose exec mysql sh -lc 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -e "SELECT id, customer_id, status, total_amount, promotion_code FROM order_service.orders ORDER BY created_date DESC LIMIT 10;"'
```

### 2. Kafka Order -> Product

Look for these log patterns:

- `Publish new order success`
- `Receive order message`
- `Updated stock for products`

You can stream the logs with:

```bash
docker compose logs -f order-service product-service
```

Verify product stock in MySQL:

```bash
docker compose exec mysql sh -lc 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -e "SELECT id, stock FROM product_service.products WHERE id IN (\"PROD-1001\", \"PROD-1002\");"'
```

### 3. Kafka Product -> Order

Look for these log patterns:

- `Locked product success`
- `Receive product`
- `success to lock product item`

Confirm the latest order status becomes `PREPARED`:

```bash
docker compose exec mysql sh -lc 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -e "SELECT id, status FROM order_service.orders ORDER BY created_date DESC LIMIT 10;"'
```

### 4. Promotion flow

Create an order with `SUMMER20`:

```bash
curl -X POST http://localhost:8282/orders/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": "CUST-VERIFY-002",
    "promotionCode": "SUMMER20",
    "orderItems": [
      { "productId": "PROD-1001", "quantity": 1 },
      { "productId": "PROD-1002", "quantity": 1 }
    ]
  }'
```

Check promotion usage after the order succeeds:

```bash
docker compose exec mysql sh -lc 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -e "SELECT code, usage_limit, start_date, end_date FROM promotion.promotions WHERE code = \"SUMMER20\";"'
```

### 5. Redis lock flow

The lock key format is:

```text
lock:products:<sorted_product_ids>
```

For a quick check, monitor Redis while creating an order:

```bash
docker compose exec redis redis-cli --scan --pattern 'lock:*'
```

Because the key is short-lived, the most reliable verification is the original debug flow:

1. Put a breakpoint in `ProductService` around the lock creation and `tryLock(...)`.
2. Trigger order creation.
3. Inspect RedisInsight at `http://localhost:8001` with `lock:*` or `lock:products:*`.
4. Resume execution and confirm the key disappears after processing.

## Local Maven runs

Each service still builds independently from its own module directory. Example:

```bash
cd 'OrderService'
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```
