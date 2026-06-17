For your project, I would not ask Codex to directly generate a diagram first.

Ask it to:

1. Read the architecture documents.


2. Extract components and interactions.


3. Validate against ADRs and Jira mappings.


4. Then generate Mermaid.



This reduces hallucinations significantly.

Prompt

You are acting as a Solution Architect.

Goal:
Generate a Component Interaction Diagram for the AWX/Ansible Tower Regression Testing Framework.

Read the following documents before starting:

1. PRD.md
2. Technical-Specification.md
3. Volume-1-Repository-Configuration-Architecture.md
4. Volume-2-Execution-Framework.md
5. Volume-3-Reporting-Resume-Checkpoint.md
6. Volume-4-AWX-Design.md
7. Volume-6-Jira-Mapping.md
8. Volume-7-Architecture-Diagrams.md
9. Data-Contracts.md

Architecture Rules:

- Follow all ADRs defined in Technical Specification.
- Follow component responsibilities exactly as documented.
- Follow Data Contracts.
- Do not invent components.
- Do not invent interactions.
- If an interaction is not documented, explicitly list it as an assumption.

Tasks

Step 1

Identify all architectural components.

For each component provide:

- Name
- Responsibility
- Inputs
- Outputs

Step 2

Identify interactions between components.

For every interaction provide:

- Source Component
- Target Component
- Data Contract Used
- Trigger/Event
- Synchronous or Asynchronous

Step 3

Validate architecture.

Verify:

- No ADR violations
- No circular dependencies
- No responsibility overlap
- No undocumented interactions

Step 4

Generate Mermaid Component Interaction Diagram.

Use:

flowchart LR

Format:

ComponentA -->|Contract/Event| ComponentB

Group components into:

- Configuration Layer
- Validation Layer
- Execution Layer
- Reporting Layer
- Checkpoint Layer
- AWX Layer

Step 5

Generate Component Interaction Matrix.

| Source | Target | Contract | Purpose |
|----------|----------|----------|----------|

Step 6

Generate Sequence Diagram for:

1. Sequential Execution Flow
2. Parallel Execution Flow
3. Resume Flow

Use Mermaid sequence diagrams.

Step 7

Generate Architecture Review.

Identify:

- Tight coupling
- Potential bottlenecks
- Single points of failure
- Future scalability concerns

Output:

1. Component Inventory
2. Interaction Matrix
3. Mermaid Component Interaction Diagram
4. Mermaid Sequence Diagrams
5. Architecture Review

Do not generate implementation code.
Only architecture artifacts.

For your framework specifically, the resulting component diagram should roughly contain components like:

Manifest
    ↓

Manifest Validator
    ↓

Environment Resolver
    ↓

Execution Context Builder
    ↓

Execution Engine
    ↓
Artifact Pipeline
    ↓

Precheck
Deploy
Sanity
Execute
Validate
Housekeeping
    ↓

Checkpoint Manager
    ↓

Reporting Framework
    ↓

Notification Framework

AWX Workflow
AWX Job Templates

This prompt is suitable for generating Volume 7-level architecture diagrams and is much more reliable than simply asking "draw component interaction diagram."
