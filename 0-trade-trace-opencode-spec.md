
Switching to a vertical slicing approach is the best way to use an agentic tool like OpenCode. When an agent builds a full feature from the database up to the UI endpoint, you can test it immediately rather than waiting days for horizontal layers to connect.
To fix the missing business logic and force a vertical, feature-by-feature structure, update your Gemma 4 prompt with this revised version.
The Revised Prompt (Vertical Slicing Focus)
Role:
You are an Expert Solution Architect and Agile Backend Designer. You excel at breaking down business use cases into vertical feature slices that agentic coding tools (like OpenCode) can build and test independently.
Objective:
Analyze the provided use case document and the "POC Selected Features" list. Generate a comprehensive OpenCode Specification Document (docs/poc-spec.md).
Context & Constraints:
 * Vertical Slicing: After the initial project setup, all implementation must be grouped vertically by feature. A single phase must contain everything needed to complete one feature: the database schema, the frozen .NET API ingestion contract, the internal business logic, and the UI consumption endpoint (REST or WebSocket).
 * Business Logic Rigor: You must explicitly define the business rules for each feature. Do not just say "process data." Specify validation rules, state transitions, data aggregations, and exact error handling.
 * Database: H2 Database configured in Oracle compatibility mode (MODE=Oracle).
 * External Dependency: The ingestion JSON contracts for the .NET team are frozen and must be strictly defined in each feature slice.
 * Tech Stack: Java, Spring Boot, Spring WebFlux, Spring Data JPA.
Output Structure Requirement:
Generate the poc-spec.md document using the exact structure below.
# OpenCode Execution Specification: 3-Tier POC (Vertical Slices)

## Overview & Architecture
[Provide a brief summary of the system, the boundaries between the .NET Adapters, Central System, and UI, and the selected mature features.]

## Phase 1: Greenfield Project Setup & Base Infrastructure
- **Dependencies:** List exact Spring Boot starters (WebFlux, Data JPA, H2 Database Driver, Lombok, etc.).
- **Base Config:** Define `application.yml` properties, specifically providing the JDBC URL for H2 in Oracle compatibility mode (`MODE=Oracle`).

## Phase 2: Vertical Slice 1 - [Insert Feature Name, e.g., Basic Telemetry Ingestion]
*Use this structure for the first selected feature.*
- **1. Database Schema & Entities:** [Define tables, columns, constraints, and relationships for this specific feature. Use Oracle-compatible types.]
- **2. Frozen Ingestion Contract (.NET -> Central):** [Define the exact `POST` endpoint, including the complete, frozen JSON request payload with data types, and the expected JSON response.]
- **3. Business Logic & Processing:** [CRITICAL: Detail step-by-step what happens when the payload is received. List validation rules, data transformations, database state changes, and what happens if data is invalid.]
- **4. UI Consumption API:** [Define the REST endpoint or WebSocket channel the UI will use to fetch this specific processed data, including the JSON response payload.]

## Phase 3: Vertical Slice 2 - [Insert Feature Name]
*Repeat the 4-part structure from Phase 2 for the next selected feature.*
- **1. Database Schema & Entities:** [...]
- **2. Frozen Ingestion Contract:** [...]
- **3. Business Logic & Processing:** [...]
- **4. UI Consumption API:** [...]

*[Continue creating a Phase for each feature in the "POC Selected Features" list]*

## Out of Scope
[List features from the document that were NOT selected to ensure the agent ignores them.]

=== POC SELECTED FEATURES ===
[Insert your targeted features here]
=== USE CASE DOCUMENT TEXT ===
[Paste the contents of your Word document here]
How to use this with OpenCode
By grouping everything vertically, your workflow with OpenCode becomes highly iterative:
 * Prompt 1: "Execute Phase 1 to set up the project." (Verify the app boots).
 * Prompt 2: "Execute Phase 2. Build the database entities, the ingestion controller, the business logic service, and the UI controller for the first feature."
 * Test: You immediately run a curl command mimicking the .NET adapter, and a second curl command mimicking the UI. If it works, commit to Git.
 * Prompt 3: "Execute Phase 3."









Here is the updated prompt reflecting your adjustments. It moves the Database Schema to Phase 2, explicitly mandates H2 in Oracle compatibility mode, and instructs the LLM to selectively incorporate high-priority mature features into the POC scope.
The Updated Prompt
Role:
You are an Expert Solution Architect and API Designer. You excel at translating business use cases into highly technical, rigid specifications that agentic coding tools (like OpenCode) can use to autonomously develop a multi-tier backend system.
Objective:
I am providing you with the text of a use case document that outlines features for an MVP and a mature application. I am also providing a specific list of POC Selected Features, which includes all MVP features plus a few critical features from the mature system that stakeholders expect in the POC.
Your task is to analyze this document and generate a comprehensive OpenCode Specification Document (docs/poc-spec.md).
Context & Constraints:
 * Greenfield Project: This is a brand-new project. Phase 1 must exclusively handle the project scaffolding and dependency management.
 * Database Strategy (Critical): The data layer must be established immediately after project setup. We are using H2 Database configured in Oracle compatibility mode.
 * External Team Dependency: The "Adapter Layer" is being built by a separate .NET team. Therefore, the JSON API contracts between the Adapter and the Central System must be treated as frozen and explicitly defined down to the data types and validation rules.
 * Tech Stack: The Central System will be built using Java, Spring Boot, Spring WebFlux, Spring Data JPA, and H2 (Oracle mode).
 * Scope Boundary: Only generate specifications for the items listed in the "POC Selected Features" list. If a mature feature is listed there, incorporate it directly into the execution phases. Do not include features from the use case document that are not on this list.
Output Structure Requirement:
Generate the poc-spec.md document using the exact structure below. Be highly specific with technical details.
# OpenCode Execution Specification: 3-Tier POC

## Overview & Architecture
[Provide a brief summary of the system based on the document, explicitly stating the boundaries between the .NET Adapters, the Java Central System, and the UI. Mention the inclusion of select mature features for the POC.]

## Phase 1: Greenfield Project Setup
- **Dependencies:** List exact Spring Boot starters (WebFlux, Data JPA, H2 Database Driver, Lombok, etc.).
- **Base Config:** Define the required `application.yml` properties, specifically providing the exact JDBC URL and properties required to run H2 in Oracle compatibility mode (e.g., `MODE=Oracle`).

## Phase 2: Database Schema & Entities
[Define the database tables, columns, constraints, and relationships. Ensure data types align with Oracle compatibility expectations.]

## Phase 3: Frozen API Contracts (.NET Adapter -> Central System)
[CRITICAL: Define the exact ingestion endpoints the .NET team will call.]
- **Endpoint:** `[HTTP Method] /api/v1/...`
- **Purpose:** [What this endpoint does]
- **Request Payload (JSON):** [Provide a complete JSON snippet with field names, data types, and required/optional flags. This contract is frozen.]
- **Response Payload (JSON):** [Provide success and failure JSON response formats.]

## Phase 4: Business Logic & Processing
[Define exactly what the Central System does when it receives data from the Adapter before saving it or exposing it. E.g., aggregations, validations, state changes.]

## Phase 5: UI Consumption APIs (REST & Selected Mature Features)
[Define the endpoints the UI will use to consume data. If WebSockets or other mature features are included in the POC Selected Features, define those specific channels, event triggers, and payload structures here alongside the initial REST endpoints.]

## Out of Scope (Future Mature Features)
[Briefly list the mature features from the document that were NOT selected for this POC, to ensure OpenCode does not attempt to build them.]

=== POC SELECTED FEATURES ===
[Insert your targeted features here. Example:
1. Adapter data ingestion to Central API (MVP)
2. REST endpoints for UI data fetch (MVP)
3. Real-time WebSocket UI updates for critical alerts (Mature feature included in POC)]
=== USE CASE DOCUMENT TEXT ===
[Paste the contents of your Word document here]
Key Changes Made:
 * Database Shift: Moved the schema definition up to Phase 2. The prompt now explicitly instructs the LLM to configure the application.yml for H2 in Oracle mode (MODE=Oracle).
 * "POC Selected Features" Variable: Instead of strictly dividing MVP and Mature, the prompt now accepts a single list of features targeted for the POC. This allows you to cherry-pick which mature features the stakeholders want to see now.
 * Merged Consumption Phase: Phase 5 now handles how the UI consumes data, whether that is via REST polling (MVP) or the specific mature features (like WebSockets) you decide to include in the POC scope.
