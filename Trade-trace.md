Here is the updated, detailed problem statement blueprint for **Project "Trade Trace"**. I have revised the core challenge, the onboarding workflow, the architectural requirements, and the FAQ to explicitly mandate the zero-code **Push Architecture** we discussed.
# Hackathon Challenge: Project "Trade Trace"
**Theme:** Enterprise FinTech, Distributed Tracing, and Event-Driven "Push" Architecture
**Duration:** 24 Hours
**Team Size:** 6 Developers
### 📖 The Backstory (Why You Are Building This)
In the high-stakes world of investment banking and capital markets, a single financial trade does not just happen in one place. A trade is booked in an upstream system, processed by a central Settlement System, and then broadcasted to a dozen downstream systems for payments, customer advices, regulatory reporting, and ledger updates.
When a multi-million-dollar trade gets stuck, panic ensues. Operations teams waste hours frantically checking logs across ten different databases just to figure out *where* the trade died. Banks desperately need a "FedEx Tracker" for financial trades—a single pane of glass that tracks the exact lifecycle of a deal as it hops from system to system.
### 🎯 The Core Challenge
Your mission is to build **Trade Trace**, a dynamic, high-throughput trade lifecycle observability platform based entirely on a **Push Architecture**.
You must build a centralized engine that ingests real-time events from various distributed systems and stitches them together to form a complete chronological journey of a trade.
**The Catch (Zero-Code Integration):** You cannot ask a 15-year-old legacy payment gateway to rewrite its core application code just to send HTTP requests to your new tracker. Your platform must ingest these real-time events via infrastructure-level "push" integrations (like database Change Data Capture, existing Kafka topics, or sidecar adapters) so that the monitored systems require zero code changes to be tracked.
### 🚀 The "Zero-Code" Dynamic Onboarding
Your platform must be built as a generic, flexible SaaS tool for the enterprise. Onboarding a legacy settlement system should take minutes via configuration, not code changes:
 1. **Register the System & Map Topology:** An admin opens the Trade Trace UI and registers a new central node (e.g., "FX Settlement Engine"). They define its specific upstream sources and downstream consumers.
 2. **Configure the Push Listener:** Instead of writing API clients, the admin configures how Trade Trace will receive the data. They select an integration method: *"Subscribe to existing Kafka Topic X"* or *"Deploy Sidecar Webhook to listen to Database Y."*
 3. **Listen & Track:** The newly configured infrastructure immediately begins pushing standard JSON events to Trade Trace. The platform automatically tracks whether a specific trade has successfully hit all its required downstream checkpoints in real-time.
### 📈 The Scope & Tiers of Success
This project is designed to scale with your team's capability. Focus on the core ingestion engine first.
**Tier 1: The Core Push Tracker (Minimum Viable Product)**
 * Build a backend REST webhook engine that can ingest pushed trade events (e.g., TRADE_BOOKED, SETTLEMENT_STARTED, PAYMENT_SENT) and save them to a database.
 * *Constraint:* Handle idempotency. If a network hiccup causes an external system to push the exact same event twice, your database must silently handle the duplicate without breaking the UI timeline.
 * Build a search UI where an operations user can paste a Trade_ID and see a chronological timeline of exactly what has happened to that trade so far.
**Tier 2: Chaos Management & Dynamic UI (Targeted Goal)**
 * Do not hardcode the system topology. Implement the UI and backend logic to allow users to dynamically create new systems, defining their specific upstream/downstream dependencies.
 * Build a visual "Delivery Tracker" UI for individual trades. If a trade requires 3 downstream systems to acknowledge it, the UI should visually show 2/3 complete, instantly highlighting the 1 system that is lagging.
 * Implement a robust data model that handles out-of-order events. Because you are relying on distributed push networks, the "Payment Sent" event might arrive *before* the "Settlement Started" event. Your timeline must sort by the source payload's timestamp, not the database insertion time.
**Tier 3: The Event-Driven SLA Monitor (Elite Level)**
 * Upgrade the backend from REST to a true Event-Driven Architecture using an event broker (like Apache Kafka) to handle massive spikes in trade volumes without crashing.
 * Implement an active **SLA (Service Level Agreement) Alerting Engine**. If the onboarding config says a trade should reach the Payment Gateway within 5 minutes of Settlement, a background sweep must automatically flag and highlight trades that have breached this time limit.
 * Stream real-time status updates to the frontend dashboard via WebSockets so the operations team sees trades moving across the screen live.
### 🛠️ The Approved Tech Stack
To mimic a modern banking environment, you must adhere to the following stack:
 * **The Core Engine (Backend):** Java 21+ and Spring Boot 3.x.
 * **Event Broker (For Tier 3):** Apache Kafka or RabbitMQ (to handle high-throughput event ingestion).
 * **Database:** PostgreSQL or MongoDB (MongoDB is highly recommended for storing flexible, unstructured JSON trade event payloads).
 * **The Web Dashboard (Frontend):** React/Next.js, Tailwind CSS.
 * **Data Visualization:** **React Flow** (highly recommended for visualizing the dynamic upstream/downstream topology during the onboarding process).
### 🏆 Evaluation & Judging Criteria

| Criteria | Description |
| :--- | :--- |
| **Architectural Flexibility** | Did you hardcode the trade statuses and systems, or did you successfully build a dynamic onboarding engine that can map *any* system topology? |
| **Push Resiliency** | How does your system handle chaos? Can it process concurrent pushes to the same trade? Does it break if events arrive out of order or duplicate payloads are pushed? |
| **Developer / Ops Experience** | How easy is it for an operations worker to search for a failed trade and immediately understand which downstream system is responsible for the failure? |
| **Throughput & Scalability** | (For Tier 3) Can your messaging layer handle a flood of thousands of trade events without dropping data? |

### ❓ Frequently Asked Questions (FAQ)
#### Architecture & Integration
 * **Why use a Push Architecture instead of having Trade Trace pull (poll) data from APIs?**
   Financial SLA monitoring requires real-time data. Polling introduces inherent delays, wastes massive amounts of compute when there are no updates, and requires writing custom API clients for every new system. A push architecture (acting as a data sink) solves all of these enterprise bottlenecks.
 * **How do we get a legacy system to "push" data if we are not allowed to change its application code?**
   You leverage the infrastructure layer. In the real world, this is done via Change Data Capture (CDC) tools like Debezium that read database logs, or by subscribing to existing Enterprise Service Bus (ESB)/Kafka topics the system already broadcasts to. *For this hackathon, assume we deploy a pre-built "Sidecar Adapter" alongside the legacy system that reads its logs and pushes the JSON to your Trade Trace API.*
 * **What does the data payload coming into Trade Trace look like?**
   You have the freedom to design the JSON schema. However, every event payload should realistically contain a tradeId, a timestamp, the sourceSystem, and the eventStatus.
#### Resiliency & Data Management
 * **What if a downstream system fails and pushes an error event?**
   Trade Trace should capture this! If the payment gateway sends a PAYMENT_FAILED event, the overall status of the trade in the UI should immediately turn red, stopping the lifecycle and alerting the user.
 * **If a trade is processed across multiple systems, how do we link the pushed events together?**
   This is a core distributed tracing challenge. You must rely on a Correlation ID (like a global tradeId) that is passed along by every upstream and downstream system when they push their status to your tracker.
 * **Can we use a relational database (SQL) for this?**
   Yes, but think carefully about your schema. Because you are building a dynamic system where different settlement engines might have different types of events and metadata, a NoSQL database (like MongoDB) or leveraging PostgreSQL's JSONB columns will give you the flexibility you need.
