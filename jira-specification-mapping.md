# 1A. Jira Requirements Mapping Matrix

## Purpose

This matrix defines:

- Documents that must be reviewed
- Components allowed to be modified
- Schemas impacted
- Test scope required

AI agents SHALL use this matrix as the primary navigation mechanism.

---

| Jira | Description | Documents | Roles | Playbooks | Schemas / Contracts | Test Scope |
|--------|--------|--------|--------|--------|--------|--------|
| US-001 | Repository Structure | TS, V1, V5 | N/A | N/A | N/A | Structure Validation |
| US-002 | Environment Configuration | TS, V1, V5 | N/A | N/A | Config Contracts | Config Resolution |
| US-003 | Execution Environment Validation | TS, V1, V5, CS | N/A | N/A | Environment Contract | Dependency Validation |
| US-004 | Credential Framework | TS, V4, V5 | N/A | N/A | Credential Contract | Credential Injection |
| US-005 | Manifest Schema | TS, V1, DC | N/A | N/A | Manifest Schema | Schema Validation |
| US-006 | Manifest Validation Role | TS, V1, V2, DC | manifest_validation | validate_manifest.yml | Manifest Schema | Unit + Validation |
| US-007 | Manifest Validation Playbook | TS, V1, V2 | manifest_validation | validate_manifest.yml | Manifest Schema | Integration |
| US-002A | Runtime Host Validation | TS, V2, V4 | runtime_host_validation | validate_runtime_host.yml | Host Validation Contract | Unit + Integration |
| US-040 | Runtime User Validation | TS, V2, V4 | runtime_user_validation | validate_runtime_user.yml | User Validation Contract | Unit + Integration |
| US-013 | Artifact Metadata Framework | TS, V1, DC | N/A | N/A | Artifact Contract | Schema Validation |
| US-014 | Cleanup Role | TS, V2, DC | cleanup | N/A | Role Result Contract | Unit |
| US-015 | Precheck Role | TS, V2, DC | precheck | N/A | Role Result Contract | Unit |
| US-016 | Sanity Role | TS, V2, DC | sanity | N/A | Role Result Contract | Unit |
| US-017 | Housekeeping Role | TS, V2, DC | housekeeping | N/A | Role Result Contract | Unit |
| US-018 | Validation Role | TS, V2, V3, DC | validate | N/A | Validation Contract | Unit + Integration |
| US-019 | Artifact Pipeline | TS, V2, V3, V7 | cleanup, precheck, deploy, sanity, execute, validate, housekeeping | execute_parallel.yml, execute_sequential.yml | Pipeline Contract | E2E |
| US-020 | Deploy Role | TS, V2, DC | deploy | N/A | Deployment Contract | Unit |
| US-021 | Artifact Download Component | TS, V2, DC | artifact_download | N/A | Download Contract | Unit |
| US-022 | Sequential Execution | TS, V2, V3, V7 | execution_engine | execute_sequential.yml | Execution Context Contract | Integration |
| US-023 | Parallel Execution Engine | TS, V2, V3, V7, DC | execution_engine | execute_parallel.yml | Worker Contract, Execution Context Contract | Unit + Integration |
| US-024 | Timeout Framework | TS, V2, V3, V7 | execution_engine | execute_parallel.yml | Timeout Contract | Unit + Integration |
| US-025 | Execution Engine Integration | TS, V2, V3, V7, DC | execution_engine | execute_parallel.yml, execute_sequential.yml | Pipeline Contract | E2E |
| US-026 | Reporting Framework | TS, V3, DC | reporting | generate_reports.yml | Artifact Report Contract | Unit + Integration |
| US-027 | Report Aggregation | TS, V3, DC | reporting | generate_reports.yml | Execution Summary Contract | Unit + Integration |
| US-028A | Publish Reports | TS, V3, V4 | reporting | generate_reports.yml | Report Contract | Integration |
| US-029 | Notification Framework | TS, V3, V4, DC | notification | notify.yml | Notification Contract | Unit + Integration |
| US-034 | Job Templates | TS, V4 | N/A | AWX Templates | N/A | Manual Validation |
| US-035 | Sequential Workflow | TS, V4, V7 | N/A | AWX Workflow | N/A | Workflow Validation |
| US-036 | Parallel Workflow | TS, V4, V7 | N/A | AWX Workflow | N/A | Workflow Validation |
| US-037 | Resume Workflow | TS, V3, V4, V7 | resume | resume_execution.yml | Checkpoint Contract | Integration |
| US-009 | Checkpoint Contract | TS, V3, DC | checkpoint | N/A | Checkpoint Contract | Contract Validation |
| US-010 | Checkpoint Manager | TS, V3, DC | checkpoint | N/A | Checkpoint Contract | Unit + Integration |
| US-011 | Checkpoint Resolver | TS, V3, V7, DC | resume | resume_execution.yml | Checkpoint Contract | Unit + Integration |
| US-012 | Resume Dispatcher | TS, V3, V7, DC | resume | resume_execution.yml | Resume Contract | Unit + Integration |

---

## Legend

TS = Technical Specification

V1 = Repository & Configuration Architecture

V2 = Execution Framework

V3 = Reporting, Resume & Checkpoint Framework

V4 = AWX Design

V5 = Agentic Coding Standards

V7 = Architecture Diagrams

DC = Data Contracts

CS = Coding Standards
