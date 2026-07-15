I actually think this is a good architectural improvement. Your framework already separates design from implementation for the Manifest, and doing the same for the other major runtime contracts will make the project much easier for Roo/Codex to work on.

I would introduce four new design Jiras:

1. Artifact Metadata Contract


2. Checkpoint Contract


3. Reporting Contract


4. Notification Contract



Then keep the implementation Jiras focused only on building the corresponding roles/frameworks.


---

US-013 – Artifact Metadata Contract

# US-013 - Artifact Metadata Contract

## Priority

P0 (Critical Path)

---

## Goal

Define the Artifact Metadata Contract that serves as the canonical runtime representation of an artifact throughout the execution lifecycle.

The Artifact Metadata Contract shall be consumed by every lifecycle role.

The contract shall define the structure, ownership, lifecycle, mutability rules and validation rules for runtime artifact state.

No implementation shall be included in this Jira.

---

## Business Value

Provides a standardized runtime contract for every artifact.

Decouples lifecycle roles from the Manifest.

Ensures every framework component operates on a consistent runtime representation.

Provides the foundation for:

- Parallel Execution
- Checkpointing
- Resume
- Reporting
- Notifications

---

## Dependencies

Requires

- Manifest Contract
- Technical Specification

Must complete before

- Artifact Metadata Builder
- Execution Engine
- Reporting
- Checkpoint Framework

---

## Scope

Define

- Artifact Metadata structure
- Field ownership
- Field descriptions
- Runtime lifecycle
- Mutability rules
- Serialization format
- Versioning strategy

---

## Artifact Metadata Structure

Define

User supplied fields

- artifactId
- artifactName
- releaseVersion
- productionVersion
- deployProduction
- runtimeUser

Resolved fields

- environment
- region

Framework generated fields

- workspace
- reportDirectory
- logDirectory
- checkpointDirectory

Runtime fields

- status
- currentStage
- retryCount
- startTime
- endTime

---

## Ownership

Document ownership for every field.

Example

Manifest

owns

artifactId

Framework

owns

workspace

Lifecycle Roles

own

status

Checkpoint Manager

owns

resume information

---

## Mutability Rules

Manifest fields

Immutable

Framework generated paths

Immutable after creation

Runtime status

Mutable

Current stage

Mutable

Execution timestamps

Mutable

Workspace

Immutable

---

## Lifecycle

Document lifecycle

Created

↓

Initialized

↓

Processing

↓

Completed

↓

Archived

---

## Validation Rules

Document

Mandatory fields

Optional fields

Default values

Allowed status values

Allowed stage values

---

## Serialization

Define

YAML

File location

Naming convention

Encoding

Versioning

---

## Deliverables

10-Data-Contracts.md

Technical Specification

Example YAML

Lifecycle Diagram

Ownership Matrix

Validation Rules

---

## Acceptance Criteria

Artifact Metadata Contract is documented.

Field ownership is documented.

Lifecycle is documented.

Mutability rules are documented.

Validation rules are documented.

Examples are provided.

---

## Definition of Done

✓ Technical Specification updated

✓ Data Contracts updated

✓ Lifecycle documented

✓ Examples added

✓ Architecture review completed


---

US-0XX – Checkpoint Contract

# Checkpoint Contract

Goal

Define the persistent checkpoint format used by the framework.

The contract shall define:

- checkpoint identifier
- artifact identifier
- current stage
- execution status
- timestamps
- retry information
- execution outcome

Scope

Only design.

No checkpoint implementation.

Deliverables

- Checkpoint Contract
- YAML Example
- State Transition Diagram
- Validation Rules

Definition of Done

Checkpoint Contract approved.


---

US-0XX – Reporting Contract

# Reporting Contract

Goal

Define the standard reporting data model.

The contract shall define

Run Summary

Artifact Summary

Stage Results

Validation Results

Execution Metrics

Timing Metrics

Error Details

Output Formats

CSV

JSON

HTML

Scope

Design only.

No report generation.

Deliverables

Reporting Contract

Example Reports

Field Definitions

Definition of Done

Reporting Contract approved.


---

US-0XX – Notification Contract

# Notification Contract

Goal

Define the notification payload exchanged between the Reporting Framework and Notification Framework.

Define

Recipients

Subject

Severity

Execution Summary

Artifact Summary

Attachment List

Status

Notification Types

SUCCESS

FAILED

PARTIAL

RESUME

Scope

Design only.

No email implementation.

Deliverables

Notification Contract

Payload Examples

State Diagram

Definition of Done

Notification Contract approved.


---

One more recommendation

Looking at how your framework has evolved, I would introduce a new section in 10-Data-Contracts.md titled Runtime Contracts:

Runtime Contracts

├── Manifest Contract
├── Resolved Environment Contract
├── Artifact Metadata Contract
├── Checkpoint Contract
├── Reporting Contract
└── Notification Contract

This makes the architecture much clearer:

Manifest Contract: External input from the user.

Resolved Environment Contract: Environment-specific configuration produced by the Environment Resolver.

Artifact Metadata Contract: Runtime state for each artifact.

Checkpoint Contract: Persisted execution state.

Reporting Contract: Structured execution results.

Notification Contract: Message payload sent to notification channels.


This layered contract model is well-suited for agentic development because every implementation Jira has a well-defined contract to implement against, reducing ambiguity for Roo/Codex.
