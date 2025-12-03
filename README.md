''
- name: Run UI Automation Tests in Interactive Mode
  hosts: windows
  gather_facts: no

  vars:
    task_name: "Run_UI_Tests"
    python_interpreter: "python.exe" # or full path if needed

  tasks:
    # Step-1: Make sure there is an active interactive console session
    - name: Detect Active Console Session ID
      win_shell: |
        $session = quser | Select-String "Active"
        if ($session) {
          ($session -split '\s+')[2]
        } else {
          ""
        }
      register: console_session

    - name: Attach to console session if not active
      win_shell: |
        $sid = quser | Select-String "{{ ansible_user }}" | % { ($_ -split '\s+')[2] }
        if ($sid) {
          tscon $sid /dest:console
        }
      when: console_session.stdout == ""

    # Step-2: Generate delayed start boundary (90 seconds ahead)
    - name: Calculate scheduled start time
      set_fact:
        start_time: "{{ '%Y-%m-%dT%H:%M:%S' | strftime(ansible_date_time.epoch | int + 120) }}"

    # Step-3: Create Scheduled Task with interactive UI
    - name: Create scheduled task to run UI python tests
      community.windows.win_scheduled_task:
        name: "{{ task_name }}"
        description: "Run UI tests in real desktop session"
        enabled: yes
        run_level: highest
        logon_type: password
        username: "{{ ansible_user }}"
        password: "{{ ansible_password }}"
        triggers:
          - type: time
            start_boundary: "{{ start_time }}"
        actions:
          - path: "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe"
            arguments: >
              -WindowStyle Maximized -Command "& {
                $env:service_account_username='{{ service_account_username }}';
                $env:service_account_password='{{ service_account_password }}';
                $env:target_environment='{{ target_environment }}';
                $env:project='{{ project }}';
                {{ python_interpreter }} '{{ python_script }}' '{{ test_report_path }}' |
                Out-File 'C:\Temp\tcoe-log.txt' -Append
              }"
        state: present
      register: task_created

    # Step-4: Run task immediately if needed
    - name: Start scheduled task immediately
      win_scheduled_task:
        name: "{{ task_name }}"
        state: running
      when: task_created is changed

    - debug:
        msg: "Task '{{ task_name }}' created and triggered successfully!"

''
[project]
name = "uat-automation"
version = "1.0.0"
description = "Robot Framework based UAT automation with Selenium"
requires-python = ">=3.10"

dependencies = [
    "robotframework>=6.1",
    "robotframework-seleniumlibrary>=6.0",
    "selenium>=4.20",
    "webdriver-manager>=4.0"
]

[tool.uv]
# optional: keeps .venv inside project root
venv = ".venv"


robotframework>=6.1
robotframework-seleniumlibrary>=6.0
selenium>=4.20
webdriver-manager>=4.0


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





