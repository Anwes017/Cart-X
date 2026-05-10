# CartX

CartX is a microservices-based ecommerce project with a React frontend, Spring Boot backend services, an API gateway, JWT authentication, cart and checkout flows, Kafka events, Redis, MongoDB, and PostgreSQL.

## Project Structure

```text
CartX/
├── frontend/              # React + Vite UI
├── api_gateway/           # Spring Cloud Gateway, JWT validation, routing
├── auth-service/          # User registration/login and JWT generation
├── product_service/       # Product catalog, stock, MongoDB, Redis cache
├── cart_service/          # Redis-backed cart service
├── order-service/         # Order persistence and payment-success consumer
├── payment_service/       # Mock checkout/payment confirmation
├── notification_service/  # Email/SMS notification consumer
└── ai_service/            # Product AI/chat integration
```

## Services

| Service | Port | Main Routes |
| --- | ---: | --- |
| Auth Service | `8080` | `/auth/**` |
| API Gateway | `8081` | Routes all public API traffic |
| Product Service | `8082` | `/products/**` |
| Cart Service | `8083` | `/cart/**` |
| Order Service | `8084` | `/orders/**` |
| Payment Service | `8085` | `/payments/**` |
| Notification Service | `8086` | Kafka consumers |
| AI Service | `8090` | AI/chat endpoints |
| Frontend | `5173` | React app |

## Prerequisites

- Java 17+ for most services
- Java 25 for `auth-service` as currently configured
- Node.js and npm
- PostgreSQL
- MongoDB
- Redis
- Kafka
- Optional for AI features: Ollama on `localhost:11434` and Chroma on `localhost:8000`

Required local databases:

- `ecommerce_auth_db` for auth
- `order_db` for orders
- `productdb` in MongoDB for products

Useful environment variables:

```bash
export JWT_SECRET="replace-with-a-long-secret"
export DB_USERNAME="postgres"
export DB_PASSWORD="your-password"
export MAIL_USERNAME=""
export MAIL_PASSWORD=""
```

## Run Backend Services

Start infrastructure first: PostgreSQL, MongoDB, Redis, and Kafka.

Then run each service from its folder:

```bash
cd auth-service && ./mvnw spring-boot:run
cd api_gateway && ./mvnw spring-boot:run
cd product_service && ./mvnw spring-boot:run
cd cart_service && ./mvnw spring-boot:run
cd order-service && ./mvnw spring-boot:run
cd payment_service && ./mvnw spring-boot:run
cd notification_service && ./mvnw spring-boot:run
cd ai_service && ./mvnw spring-boot:run
```

## Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

The frontend proxies API calls through the gateway at `http://localhost:8081`.

## Build Checks

Frontend:

```bash
cd frontend
npm run build
```

Backend service compile check:

```bash
cd api_gateway && ./mvnw -q -DskipTests package
cd auth-service && ./mvnw -q -DskipTests package
cd product_service && ./mvnw -q -DskipTests package
cd cart_service && ./mvnw -q -DskipTests package
cd order-service && ./mvnw -q -DskipTests package
cd payment_service && ./mvnw -q -DskipTests package
cd notification_service && ./mvnw -q -DskipTests package
cd ai_service && ./mvnw -q -DskipTests package
```

## Notes

- The gateway validates JWTs and forwards the authenticated email as `X-User-Id` to protected services.
- Product reads are public; product create/update/delete are guarded by backend role checks.
- Payment is currently a mock flow that publishes `payment-success` events for downstream order handling.
