Yes. For Codex/Roo, use the following as the Technical Specification Addendum and append it to the Technical Specification document that was generated earlier.


---

22. Architecture Decision Records (ADR)

ADR-001 Runtime Host Selection

Status

Approved

Context

Regression execution hosts vary between executions and environments.

Maintaining static inventory entries for all potential execution hosts creates operational overhead.

Decision

Execution hosts shall be provided at runtime through AWX survey inputs.

Hosts shall be dynamically injected into the execution inventory.

Consequences

Benefits:

No inventory maintenance

Supports dynamic host selection

Improves scalability

Tradeoffs:

Additional runtime validation required


---

ADR-002 Environment Configuration Separation

Status

Approved

Context

Environment configuration and host selection were previously coupled through inventory definitions.

Decision

Environment configuration shall be stored separately from runtime host definitions.

Configuration hierarchy:

all.yml
    ↓
platform.yml
    ↓
environment.yml
    ↓
manifest

Consequences

Benefits:

Cleaner configuration management

Host independence

Simplified onboarding


---

ADR-003 Filesystem-Based Checkpoints

Status

Approved

Context

Resume capability requires execution state persistence.

Database-based solutions introduce operational dependencies.

Decision

Execution state shall be stored using filesystem checkpoint markers.

Example:

payment-service.completed
billing-service.failed

Consequences

Benefits:

No database dependency

Simple implementation

Easy troubleshooting

Tradeoffs:

Checkpoint directory must remain available


---

ADR-004 Internal Parallel Execution Engine

Status

Approved

Context

Parallel execution could be implemented using:

AWX Parallel Workflow Branches

or

Internal Execution Engine

Decision

Parallelism shall be implemented inside a dedicated execution engine.

AWX shall orchestrate only a single execution job.

Consequences

Benefits:

Simpler reporting

Simpler checkpointing

Simpler resume

Dynamic artifact counts


---

ADR-005 JSON-Based Reporting Contracts

Status

Approved

Context

Reporting data must be consumable by:

Humans

Automation

Future dashboards

Decision

All reports shall be generated using JSON contracts.

Consequences

Benefits:

Machine readable

Easy aggregation

Future dashboard compatibility


---

ADR-006 AWX as Orchestration Layer Only

Status

Approved

Context

AWX provides orchestration and credential management.

Using AWX as a data store introduces coupling.

Decision

AWX shall not store:

Checkpoints

Reports

Configuration

AWX shall only provide:

Execution

Credentials

Notifications

Workflow orchestration


---

23. Component Responsibility Matrix

Configuration Framework

Responsibilities

Load configuration hierarchy

Resolve variables

Provide environment configuration

Must Not

Execute artifacts

Generate reports

Manage checkpoints


---

Manifest Framework

Responsibilities

Validate manifests

Load runtime inputs

Provide execution context

Must Not

Perform deployments

Perform validation


---

Runtime Host Validation Framework

Responsibilities

Validate host format

Validate connectivity

Validate runtime access

Must Not

Load configuration

Deploy artifacts


---

Execution Engine

Responsibilities

Artifact scheduling

Worker allocation

Artifact lifecycle execution

Timeout supervision

Status tracking

Checkpoint updates

Must Not

Generate reports

Send notifications

Resolve configuration


---

Deployment Framework

Responsibilities

Download artifacts

Deploy artifacts

Verify deployment

Must Not

Perform validation

Generate reports


---

Validation Framework

Responsibilities

Compare outputs

Execute validation scripts

Generate validation results

Must Not

Deploy artifacts

Modify checkpoints


---

Reporting Framework

Responsibilities

Generate artifact reports

Generate validation reports

Generate execution summary

Must Not

Execute artifacts

Modify checkpoints

Send notifications


---

Checkpoint Framework

Responsibilities

Persist execution state

Provide resume metadata

Maintain checkpoint markers

Must Not

Generate reports

Execute validation


---

Resume Framework

Responsibilities

Load checkpoint state

Determine restart position

Dispatch resumed execution

Must Not

Modify reports

Modify completed artifact state


---

Notification Framework

Responsibilities

Generate notification payloads

Trigger notifications

Must Not

Inspect execution directories

Execute artifacts


---

24. Component Interaction Rules

Rule CI-001

Execution Engine may call:

Deployment Framework

Validation Framework

Checkpoint Framework


---

Rule CI-002

Reporting Framework may consume:

Artifact Reports

Validation Reports

It shall not consume checkpoint files.


---

Rule CI-003

Resume Framework may consume:

Checkpoint Files

It shall not consume reports.


---

Rule CI-004

Notification Framework may consume:

Execution Summary

It shall not read artifact workspaces.


---

Rule CI-005

AWX shall invoke:

Manifest Validation

Host Validation

Execution Engine

Reporting

Notifications

AWX shall not directly manipulate checkpoints.


---

25. Architectural Constraints

AC-001

No static inventory host dependency.


---

AC-002

Runtime host selection mandatory.


---

AC-003

One workspace per artifact.


---

AC-004

One checkpoint directory per run.


---

AC-005

One report directory per run.


---

AC-006

Resume only at artifact boundaries.


---

AC-007

Credentials stored only in AWX.


---

AC-008

Reporting independent of AWX APIs.


---

AC-009

Checkpointing independent of AWX APIs.


---

AC-010

Execution Engine owns all parallel execution decisions.


---

26. Source of Truth Hierarchy

If conflicts exist between documents, precedence shall be:

Volume 1-7 Implementation Specifications
        ↓
Technical Specification
        ↓
PRD

AI agents shall always follow the highest-precedence document.


---

27. AI Agent Implementation Rules

Before implementing any Jira story:

1. Read Technical Specification

2. Read Volumes 1-7

3. Read Jira Mapping (Volume 6)

4. Implement only impacted components

5. Generate tests

6. Update documentation

The agent shall not introduce new architectural patterns without updating ADRs.


---
