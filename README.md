# User Events Kafka Service

This project demonstrates a Spring Boot application integrating with Apache Kafka to handle user events (specifically
user registration). It includes a Producer that publishes events and a Consumer that listens for them, simulating a
real-world asynchronous messaging scenario.

## 📋 Table of Contents

- [Overview](#overview)
- [Apache Kafka Features](#apache-kafka-features)
- [Prerequisites](#prerequisites)
- [Running the Environment](#running-the-environment)
- [Docker Compose Explained](#docker-compose-explained)
- [Key Concepts & Annotations](#key-concepts--annotations)
- [Manual Testing (CLI)](#manual-testing-cli)
- [Architecture](#architecture)
- [Tips & Tricks](#tips--tricks)

---

## 🚀 Overview

In a microservices architecture, services often need to communicate asynchronously. This project uses Kafka to decouple
the **User Registration** process from the **Notification** process.

- **Producer**: Publishes a `UserRegisteredEvent` when a user registers.
- **Consumer**: Listens to the topic and simulates sending a confirmation email.

## 🏗 Architecture Diagram

![Architecture](https://github.com/badripaudel77/users-events-kafka/blob/main/images/workflow_architecture_kafka.png?raw=true)

How Kafka helps in a User Events Service:

**Flow:**

1. User registers via the API.
2. Application saves user to DB.
3. **Producer** sends a message to Kafka topic `user.emailevents`.
4. The API responds immediately to the user (Low Latency).
5. **Consumer** picks up the message asynchronously and handles the slow process (sending email).

---

---

## 🌟 Apache Kafka Features

Apache Kafka is a distributed event streaming platform known for its performance and reliability. Here are some key
characteristics:

* **High Throughput**: Capable of handling millions of messages per second, making it suitable for big data and
  high-traffic applications.
* **Low Latency**: Delivers messages in milliseconds, which is critical for real-time systems.
* **Scalability**: It is a distributed system that scales horizontally by adding more brokers (servers) to the cluster.
* **Durability & Reliability**: Messages are persisted to disk and can be replicated across multiple brokers to ensure
  no data is lost.
* **Decoupling**: Producers and Consumers are fully decoupled. The producer doesn't need to know who is consuming the
  data, and the consumer doesn't need to know who produced it.
* **Log-Based Storage**: Unlike traditional message queues that remove messages once consumed, Kafka stores messages in
  an append-only log. This allows consumers to read at their own pace or replay historical data.

---

## 🛠 Prerequisites

- **Java 17+** (I used 25 though)
- **Maven**
- **Apache Kafka 4.1.1** (as in the docker file - It uses KRaft, no Zookeeper.)
- **Docker & Docker Compose** (for running Kafka and Kafka UI)

---

## 🐳 Running the Environment

We use Docker Compose to spin up a Kafka broker and a UI management tool.

1. **Start the services:**
   Open your terminal in the project root and run:
   ```bash
   docker-compose up --build
   ```
   **NOTE:** You can append -d if you want to run in detached mode but you won't see any ongoing logs.

2. **Verify containers are running:**
   ```bash
   docker ps
   ```
   You should see `kafka` and `kafka-ui` containers.

3. **Access Kafka UI:**
   Open your browser and go to [http://localhost:8080](http://localhost:8080).
   Here you can view topics, messages, and consumer groups visually.

---

## 🔍 Docker Compose Explained

The `docker-compose.yml` file sets up the Kafka ecosystem. Here are the key components:

### **Kafka Service (`kafka`)**

* **Image**: `apache/kafka:latest` (Runs in KRaft mode, no Zookeeper needed).
* **Ports**:
    * `9094`: **External Port**. Used by your Spring Boot application running on your host machine (localhost) to
      connect to Kafka.
    * `9092`: **Internal Port**. Used for communication inside the Docker network (e.g., Kafka UI talking to Kafka).
    * `9093`: **Controller Port**. Used for Kafka's internal cluster management.
* **Listeners**:
    * `PLAINTEXT://:9092`: For internal network traffic.
    * `EXTERNAL://:9094`: For external traffic (your laptop).

### **Kafka UI Service (`kafka-ui`)**

* **Port**: `8080`.
* **Configuration**: Connects to the Kafka broker using the internal hostname `kafka` and port `9092`.

---

## 🔑 Key Concepts & Annotations

This project uses **Spring for Apache Kafka**. Here are the important annotations used in the API:

### 1. `@KafkaListener`

Found in `UserEventConsumer.java`.

```java

@KafkaListener(topics = "user.emailevents", groupId = "user-event-group")
public void consume(UserRegisteredEvent event) { ...}
```

* **Purpose**: Marks a method to be the target of a Kafka message listener on the specified topics.
* **`topics`**: The Kafka topic to subscribe to (`user.emailevents`).
* **`groupId`**: Identifies the consumer group. Kafka load-balances partitions across consumers in the same group.

### 2. `KafkaTemplate`

Used in `UserEventProducer.java`.

```java
private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;
```

* **Purpose**: A template class that wraps the Kafka Producer and provides high-level methods to send data to topics.

### 3. `@Component` / `@Service`

* Standard Spring annotations to register the Producer and Consumer classes as beans in the Spring context so they can
  be injected and managed.

---

## 🛠 Manual Testing (CLI)

You can interact with Kafka directly from the terminal by SSH-ing into the container.

### 1. SSH into the Kafka Container

```bash
docker exec -it kafka /bin/bash
# If /bin/bash doesn't work, try:
# docker exec -it kafka sh
```

### 2. Navigate to Kafka Binaries

Usually located in `/opt/kafka/bin/` or directly available in the path.

### 3. List Topics

```bash
kafka-topics.sh --bootstrap-server localhost:9092 --list
```

### 4. Start a Console Producer

Send messages manually to the topic.

```bash
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic user.emailevents
> {"id":1, "name":"John Doe"}
> {"id":2, "name":"Jane Doe"}
```

*(Note: Since your app expects JSON/Objects, sending raw strings might cause deserialization errors in the app, but it
verifies the broker works).*

### 5. Start a Console Consumer

Read messages from the topic.

```bash
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic user.emailevents --from-beginning
```

---

## 💡 Tips & Tricks

* **Kafka UI is your friend**: Instead of remembering CLI commands, use `localhost:8080` to inspect messages, reset
  offsets, or delete topics.
* **Serialization**: Ensure your Producer and Consumer share the same Model (or compatible JSON structure). If you
  change the package name of the class, the default Type ID mapping might fail unless configured.
* **Consumer Groups**: If you want multiple instances of your app to share the load, keep the `groupId` the same. If you
  want all instances to receive the same message (broadcast), use different `groupId`s.
* **Docker Networking**: Notice `KAFKA_ADVERTISED_LISTENERS`. This is the most common source of pain. It tells clients
  exactly where to find the broker. We use `localhost:9094` for your IDE and `kafka:9092` for internal containers.


---
# 🚀 JVM Observability Stack: Actuator + Prometheus + Grafana

This project implements a full "Observability Pipeline." 
This part explains how these three tools work together to monitor your Spring Boot application's health, 
performance, and Kafka integration.

---

## 🏗️ The Architecture

1. **Spring Boot (The Producer):** Generates internal data.
2. **Prometheus (The Collector):** Periodically pulls and stores that data.
3. **Grafana (The Visualizer):** Queries Prometheus to create graphs and alerts.

---

## 🛠️ 1. Spring Boot Actuator
**Concept:** Actuator turns your application into an "open book" by exposing HTTP endpoints that reveal internal state.

### Why we need it:
* **Visibility:** Without it, you cannot see memory usage, thread counts, or HTTP traffic.
* **Standardization:** It uses **Micrometer**, a library that acts like "SLF4J but for metrics," allowing you to ship data to Prometheus, Datadog, or New Relic without changing your code.

### Important Endpoints:
* `/actuator/health`: Checks if the app and its dependencies (DB, Kafka) are alive.
* `/actuator/prometheus`: The raw data feed formatted specifically for Prometheus scraping.

---

## 🗄️ 2. Prometheus
**Concept:** A Time-Series Database (TSDB) that uses a **Pull Model**. Instead of your app "sending" data, Prometheus "scrapes" your app.

### Why we need it:
*  Actuator only shows a snapshot of *now*. Prometheus stores months of data so you can analyze trends (e.g., "Why did the server crash last Sunday?").
*  Prometheus allows you to filter metrics by tags (e.g: see request counts for only the `/api/v1/users` endpoint).

### Key Concepts:
* **Scrape Interval:** How often Prometheus visits your app (like 15s or 5s).
* **PromQL:** The query language used to calculate rates and averages from raw data.

---

## 📊 3. Grafana
**Concept:** The "Single Pane of Glass." It is a UI that connects to Data Sources to render beautiful, interactive dashboards.

### Why we need it:
* You can view Kafka consumer lag and JVM memory on the same screen to see if they are related.
* You can configure Grafana to send Slack/Email notifications if a metric (like Error Rate) crosses a threshold.

---

## 📈 Dashboard & Query Cheat Sheet

### 🚀 Fastest Setup: Import by ID
In Grafana, go to **Dashboards > Import** and use these IDs to get professional layouts instantly:

| ID | Name | Focus |
| :--- | :--- | :--- |
| **11378** | **Spring Boot 3.x** | The best overall dashboard for Spring Boot 3 + Micrometer. |
| **4701** | **JVM Micrometer** | Focuses on Garbage Collection and Memory "Sawtooth" patterns. |
| **20784** | **Kafka Consumer** | Essential for monitoring Consumer Lag and throughput. |

### 🔍 Top 3 Essential Queries (PromQL)
Paste these into the **Explore** tab to see your data manually:

1. **Traffic Volume (Requests Per Second):**
   `sum(rate(http_server_requests_seconds_count[1m]))`

2. **Memory Health (Heap Usage %):**
   `jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}`

3. **Kafka Lag (How far behind is my consumer?):**
   `sum(kafka_consumer_fetch_manager_records_lag_max)`

---

## 🛠️ Maintenance & Troubleshooting
* **Status "UP" but no data:** Ensure your `prometheus.yml` targets the correct port (`8081` vs `8080`).
* **Dashboard showing "N/A":** Ensure you have the `micrometer-registry-prometheus` dependency in your `pom.xml`.
* **Grafana Error "Failed to fetch":** This is often a browser cache issue with the "Drilldown" plugin. Clear site data or use Incognito mode.

---
