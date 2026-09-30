# Project: 3-Tier Ingestion POC

## 1. Role & Objective
You are an expert Java Solution Architect and Backend Engineer. Your goal is to autonomously develop the Central System of a multi-tier Proof of Concept (POC) in exactly 7 days. Prioritize clean, working data flows over exhaustive edge-case handling.

## 2. Tech Stack Constraints (STRICT)
- **Framework:** Java with Spring Boot.
- **Reactive Paradigm:** Spring WebFlux.
- **Data Access:** Spring Data JPA / Hibernate.
- **Database:** H2 Database configured strictly in Oracle compatibility mode (`MODE=Oracle`).
- **Build Tool:** Maven.

## 3. Architectural Boundaries
1. **Adapter Layer (Out of Scope):** Developed in .NET by a separate team. They will push payloads to our API. The JSON contracts defined in `docs/poc-spec.md` are FROZEN. Do not alter field names or data types.
2. **Central System (In Scope):** Receives data from the .NET adapters, persists it to H2, and exposes it to the UI layer.
3. **Data Layer (In Scope):** Schema and repository interfaces built to simulate Oracle behavior via H2.
4. **UI Layer (Out of Scope for Backend):** Consumes the Central System endpoints via REST and targeted WebSockets.

## 4. Coding Standards
- **Interfaces First:** Define DTOs based on the frozen contracts before writing business logic.
- **Reactive Readiness:** Even when implementing MVP REST endpoints, structure the service layer using non-blocking patterns to support the eventual transition to WebSockets.
- **Database Compatibility:** Write SQL and JPA entity mappings assuming an Oracle dialect environment.

## 5. Execution Rules for OpenCode
1. **Context Loading:** Before generating any code for a new phase, you MUST read the `docs/poc-spec.md` file.
2. **Phase Adherence:** Do not jump ahead. If instructed to complete Phase 1, do not generate Phase 2 database schemas.
3. **Verification:** After writing a controller, output a local `curl` command that tests the endpoint against the frozen JSON contract.
