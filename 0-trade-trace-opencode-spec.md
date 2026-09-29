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
