
#  System Flow Summary: Deal-Based Batch Processing Architecture

##  System Overview

The system processes large volumes of files representing *deals*. Each file undergoes a 3-stage processing pipeline:
1. **File Parsing**
2. **Deal Processing**
3. **FIML/XML Generation**

Each stage is handled by separate Spring components and runs asynchronously on custom executors. The system ensures:
- Deal-wise task isolation and ordering.
- Multi-threaded processing within and across stages.
- Queueing, locking, and rejection handling.
- Per-deal and per-thread logging support.

##  File Ingestion and Watcher Service

- A **Java WatchService** monitors the incoming directory.
- Overflow is handled via a **fallback re-scan sync** on event loss.
- A **BlockingQueue** backs file intake to ensure backpressure.

##  Deal Hash Routing  Queuing

- A **`ConsistentHashingTaskExecutor`** routes tasks to one of N deal executors using:
  ```java
  int index  Math.abs(dealId.hashCode()  numExecutors);
  ```
- Each executor is a **single-threaded `ThreadPoolTaskExecutor`**, ensuring per-deal serial execution.
- Tasks rejected due to queue overflow fall back to the caller thread (`CallerRunsPolicy`).

##  Executor Configuration

Each stage uses:
- Its **own configurable executor** (via `ThreadPoolTaskExecutor`).
- **Custom `TaskDecorator`** for MDC propagation and logging context.
- Graceful shutdown with `shutdown()` and `awaitTermination()`.

##  3-Stage Processing Pipeline

Each stage runs on its own executor:

1. **FileProcessor**
   - Extracts deal metadata (e.g., System ID, Version) using `BufferedReader`.
   - Submits task to `ConsistentHashingTaskExecutor`.

2. **DealProcessor**
   - Triggered based on DB state (`deal_status`).
   - Processes and transforms data.
   - Submits task to `ConsumerExecutor`.

3. **OMR/XmlGenerator**
   - Generates FIML or XML output using processed data.

Each component reads/writes to DB, updating deal status between stages.

##  Event-Driven Coordination

- System uses **DB as a status event bus**.
- Pollers or schedulers check for deals in a specific status and enqueue them for the next stage.
- Stage transitions depend on deal status (`deal_ready`, `processed`, `omr_ready`, etc.).

##  Logging Infrastructure

- Uses **Logback with async  sifting appenders**.
- Log files are grouped by **deal executor thread** (not deal itself).
- **MDC propagation** ensures all logs from `ConsumerExecutor` tasks are included in the right deal executor's log.

##  MDC Propagation Design

1. **Deal Executors**
   - Set `MDC.put("dealThread", threadName)` using a `TaskDecorator`.

2. **Consumer Executor**
   - Uses a separate `TaskDecorator` to copy MDC context from parent thread.
   - Enables proper log routing without coupling executor implementations.

##  File Movement Logic

- Files are moved between directories (`tmp`, `fileprocessed`, `done`, `skipped`, etc.) based on `deal_status`.
- Status enums are enriched to contain:
  - `SourceDirectory`
  - `TargetDirectory`
- Routing logic is dynamic and can evolve with new statuses.

##  Performance and Profiling

- CPU and memory bottlenecks are monitored via **IntelliJ Profiler**.
- Profiling showed:
  - DB access as a potential bottleneck (`OMRDataProvider`).
  - Need to optimize thread pool sizes.
- Goal: **300 files/minute** throughput.
- Threads: Carefully sized per stage (3050 threads total) to avoid starvation or contention.

##  Graceful Shutdown

- Executors configured to:
  - Call `shutdown()`.
  - Wait using `awaitTermination(...)`.
- Ensures all queues are empty before process exits.

##  Concurrency Management

- **Locking per deal** during processing ensures:
  - No duplicate processing of same deal.
  - Only the first file of a deal is processed; others are skipped once deal is handled.
- In-memory locks or DB state used depending on the stage.

##  Tools  Libraries

- Spring Boot (3)
- Spring Async (`Async`)
- Spring JDBC
- Jersey
- Micrometer (for thread naming)
- Logback (async  MDC)
- WatchService (file monitoring)
- Custom `ConsistentHashingTaskExecutor`
