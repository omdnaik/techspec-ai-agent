# TechSpec AI Agent


-Djdk.internal.httpclient.disableHostnameVerification

This project analyzes Java Spring Boot code, SQL DDLs, and generates technical documentation in Markdown format.

# System Architecture Summary

## 1. Overview

This system processes financial deal files through a multi-stage asynchronous pipeline using Spring Boot. The pipeline includes parsing, processing, and XML generation phases. Each stage operates with its own executor and can handle tasks concurrently while maintaining deal-level sequential consistency.

## 2. Component/Class Overview

| Component                       | Responsibility                                                               |
| ------------------------------- | ---------------------------------------------------------------------------- |
| `FileWatcherService`            | Monitors directories for new files and initiates processing.                 |
| `FileProcessorService`          | Parses raw deal files and extracts identifiers.                              |
| `DealProcessorService`          | Transforms parsed deal data using business rules.                            |
| `XmlGeneratorService`           | Converts transformed data into the final XML format.                         |
| `ConsistentHashingTaskExecutor` | Ensures all tasks for a single deal go to the same single-threaded executor. |
| `DealFileQueueManager`          | Queues and serializes processing of files per deal.                          |
| `LoggingConfig`                 | Sets up executor-based and thread-based loggers.                             |

## 3. Threading & Concurrency Design

* Each major component uses a dedicated `ThreadPoolTaskExecutor`.
* The `ConsistentHashingTaskExecutor` hashes `dealId` to one of N single-threaded executors.
* MDC and `TaskDecorator` are used to propagate logging context.
* Executors use `CallerRunsPolicy` to avoid silent task drops.

## 4. Directory & File Flow

| Deal Status  | Source Dir       | Destination Dir  |
| ------------ | ---------------- | ---------------- |
| `RECEIVED`   | `/input`         | `/tmp`           |
| `DEAL_READY` | `/tmp`           | `/fileprocessed` |
| `PROCESSED`  | `/fileprocessed` | `/xml`           |
| `ERROR`      | Any              | `/error`         |

### Special Case: `DEAL_READY`

* From `FileProcessorService`: Move from `/tmp` → `/fileprocessed`.
* From `DealProcessorService`: Move from `/fileprocessed` → `/tmp`.

## 5. Status Routing Logic (Enum-Based)

```java
public enum DealStatus {
    DEAL_READY {
        public Path getSource(ProcessingStage stage) {
            return stage == FILE_PROCESSING ? TMP : FILE_PROCESSED;
        }
        public Path getDestination(ProcessingStage stage) {
            return stage == FILE_PROCESSING ? FILE_PROCESSED : TMP;
        }
    }
    // ... other statuses
}
```

## 6. Logging Strategy

* Executors are prefixed (e.g., `dealExecutor-1`, `fileExecutor-1`).
* MDC stores `threadName` as `dealThread`.
* Sifting Appender writes logs per thread to `logs/dealExecutor-1.log`, etc.
* `ConsumerExecutor` logs are routed via MDC propagation.

## 7. Performance Targets

* **Files:** 10,000
* **Duration:** 8 hours
* **Target:** \~300 files/minute
* **Threads per stage:** Max 5 (configurable)
* **Deal-level concurrency:** Sequential per deal

## 8. Key Constraints

* Spring Boot batch job (non-web)
* Spring JDBC with HikariCP
* Spring Boot starter for Jersey REST (for optional APIs)
* Conditional secondary DB (enabled by XML property at startup)
* Not using Spring Batch

## 9. Error Handling & Recovery

* Exceptions during processing are caught and status is set to `ERROR`
* Files moved to `/error` dir
* Overflow events from `WatchService` trigger fallback sync
* Fallback sync avoids re-queuing already queued files

## 10. Java Watcher Overflow Handling

* Queue overflow is expected under high volume
* Uses fallback sync thread to scan and enqueue files manually
* Files already in executor queues are skipped

## 11. Logging Pipeline Flow

```java
DealExecutor → submits → ConsumerExecutor
           ↳ MDC Decorator adds `dealThread`
ConsumerExecutor ↳ inherits MDC context
Logs from both go to `logs/dealExecutor-<X>.log`
```

## 12. Suggestions for Future Enhancements

* Replace DB polling with Event-Driven messaging
* Introduce in-memory state coordination (e.g., Redis/MapDB)
* Explore batching file tasks by deal instead of individual
* Log-level telemetry using Micrometer + Grafana dashboards

## 13. Monitoring & Observability

* Thread counts, queue sizes, rejection metrics (via custom meters)
* Log volume per deal executor
* Async appender health
* Disk I/O during fallback sync

## 14. Other Notes

* Uses conditional bean creation and `@Primary` for DB routing
* Uses fallback sync with `BlockingQueue` where Watcher fails
* Extensible `Enum` status routing strategy
* 

Here's a structured prompt you can use to prepare a presentation for a meeting:


---

Prompt Template
"I need help creating a presentation for an upcoming meeting. Here are the details:

Meeting Objective: [e.g., present Q2 results, propose a new project, align on strategy]

Audience: [e.g., senior leadership, project team, clients]

Time Limit: [e.g., 10 minutes, 30 minutes]

Key Points to Cover:

1. [e.g., performance metrics]


2. [e.g., challenges and solutions]


3. [e.g., next steps]



Tone and Style: [e.g., formal, persuasive, informative, concise]

Visual Aids Needed: [e.g., charts, graphs, screenshots, bullet points]

Any Specific Tools/Format Required: [e.g., PowerPoint, Google Slides]


Please help me outline the presentation and suggest slide titles with key content for each."


---

Would you like me to fill in this prompt with your current meeting details?



Here are 2–3 high-level points suitable for functional managers:


---

🔹 Key Highlights for Functional Managers

1. Fast & Reliable Deal Processing

Processes thousands of financial deal files per hour with guaranteed accuracy and traceability.



2. Modular & Extensible Design

Easily supports new deal types, validation rules, and transformation logic without impacting existing flows.



3. Automated Error Handling & Recovery

Built-in safeguards ensure no deal is lost; issues are isolated and logged for review.





---

Let me know if you'd like this phrased for email, slide, or status report.





