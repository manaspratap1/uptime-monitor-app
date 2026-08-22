# Distributed Uptime & Health Monitoring Engine

A cloud-native, event-driven backend system designed to monitor the availability of distributed HTTP endpoints, perform concurrent health checks via AWS SQS, cache status snapshots in Redis, and dispatch real-time incident notifications via AWS SNS.

---

## High-Level Architecture

<img width="7674" height="3930" alt="image" src="https://github.com/user-attachments/assets/118bc2f7-9ddd-4c3a-a59e-4bd74eb99aec" />

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
* **POST** `/api/endpoints`
* **Rate Limit:** 5 requests / minute per IP
```json
{
  "url": "[https://example.com](https://example.com)",
  "alertEmail": "devops-lead@company.com"
}
