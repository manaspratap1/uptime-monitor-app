# Distributed Uptime & Health Monitoring Engine

A cloud-native, event-driven backend system designed to monitor the availability of distributed HTTP endpoints, perform concurrent health checks via AWS SQS, cache status snapshots in Redis, and dispatch real-time incident notifications via AWS SNS.

---

## High-Level Architecture

<img width="787" height="686" alt="image" src="https://github.com/user-attachments/assets/f4172025-c3c9-478c-b3b1-76fe8f80e9b7" />



---

## Engineering Highlights

* **Decoupled Job Queueing:** Instead of synchronously pinging URLs inside an internal thread pool, a lightweight cron dispatcher offloads check payloads to **AWS SQS**, enabling horizontal scaling of worker consumers.
* **Fault Isolation & Alerting:** Monitored endpoint failures (timeouts, 5xx server errors, DNS resolution failures) trigger event-driven alerts published to an **AWS SNS Topic** to fan out incident notifications.
* **Atomic API Rate Limiting:** Implemented a fixed-window counter interceptor backed by **Redis** to protect registration routes against request flooding (returns `429 Too Many Requests`).
* **Sub-Millisecond Read Path:** Health check results are written to **Redis** with a 5-minute TTL. Endpoint status queries hit the cache first, falling back to PostgreSQL only on a cache miss.
* **Containerized Infrastructure:** Entire stack (App, PostgreSQL, Redis, LocalStack/AWS SDK) is orchestrated locally using Docker Compose with automated health checks.

---

## Tech Stack

* **Language & Framework:** Java 17, Spring Boot 3.x / 4.x, Spring Data JPA, Spring Cloud AWS
* **Storage & Caching:** PostgreSQL 15, Redis 7
* **Cloud Infrastructure:** AWS SQS, AWS SNS, LocalStack (Local Dev)
* **DevOps:** Docker, Docker Compose, Nginx

---

## API Endpoints

### 1. Register Endpoint for Monitoring

- **POST** `/api/endpoints`
- **Rate Limit:** 5 requests / minute per IP

```json
{
  "url": "https://example.com",
  "alertEmail": "devops-lead@company.com"
}
```

**Response:** `200 OK` or `429 Too Many Requests` if the rate limit is exceeded.

### 2. Fetch Cached Endpoint Status

- **GET** `/api/endpoints/{id}/status`

**Response:** `UP` | `DOWN` | `PENDING`

Served directly from the Redis cache, with PostgreSQL as the fallback on a cache miss.

---

## Local Development & Setup

### Prerequisites

- Docker & Docker Compose
- Java 17 & Maven

### 1. Clone & Start Full Stack

```bash
git clone https://github.com/manaspratap1/uptime-monitor-app.git
cd uptime-monitor-app
docker-compose up --build -d
```

### 2. Initialize Local AWS Infrastructure (LocalStack)

```bash
docker exec -it uptime-localstack awslocal sqs create-queue \
  --queue-name uptime-check-queue \
  --region ap-south-1

docker exec -it uptime-localstack awslocal sns create-topic \
  --name uptime-alerts \
  --region ap-south-1
```

### 3. Verify Running Containers

```bash
docker ps
```

You should see the following containers:

- `uptime-app`
- `uptime-postgres`
- `uptime-redis`
- `uptime-localstack`

### 4. Verify Application Logs

```bash
docker logs -f uptime-app
```

### 5. Stop the Full Stack

```bash
docker-compose down
```

### 6. Rebuild After Code Changes

```bash
docker-compose down
docker-compose up --build -d
```

---
