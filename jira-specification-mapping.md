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


# 1B. Agent Prompt Template

For every Jira implementation:

1. Identify the Jira in the Jira Requirements Mapping Matrix.

2. Read only the documents listed in the Documents column.

3. Modify only the Roles, Playbooks and Schemas listed for the Jira.

4. Follow:
   - Technical Specification
   - Data Contracts
   - Coding Standards

5. Start with Design.

6. Wait for approval before implementation.

7. Generate:
   - Code
   - Tests
   - Documentation

8. Do not modify components outside the Jira scope.




# Replace Section 39 - Agent Story Workflow

## 39. Agent Story Workflow

For every Jira:

1. Read Jira Requirements Mapping Matrix

2. Read referenced specifications

3. Produce Design

4. Wait for Approval

5. Generate Code

6. Generate Unit Tests

7. Generate Integration Tests (if required)

8. Update Documentation

9. Execute Tests

10. Run Review

11. Produce Implementation Summary

----
# Replace Section 40 - Definition of Done

## 40. Definition of Done

A Jira implementation is complete only when:

✓ Code generated

✓ Unit tests generated

✓ Integration tests generated (where required)

✓ Documentation updated

✓ Story acceptance criteria satisfied

✓ No Data Contract violations

✓ No Coding Standards violations

✓ No Technical Specification violations

✓ Review completed

✓ All required tests passing

# Add New Section After 1A Matrix

## 1C. Critical Implementation Order

### Phase 1 - Foundation

US-001 Repository Structure

US-002 Environment Configuration

US-004 Credential Framework

US-005 Manifest Schema

US-013 Artifact Metadata Framework

---

### Phase 2 - Validation

US-006 Manifest Validation Role

US-007 Manifest Validation Playbook

US-002A Runtime Host Validation

US-040 Runtime User Validation

US-003 Execution Environment Validation

---

### Phase 3 - Lifecycle Roles

US-014 Cleanup Role

US-015 Precheck Role

US-020 Deploy Role

US-021 Artifact Download Component

US-016 Sanity Role

US-018 Validation Role

US-017 Housekeeping Role

---

### Phase 4 - Reporting

US-026 Reporting Framework

US-027 Report Aggregation

US-028A Publish Reports

---

### Phase 5 - Checkpoint & Resume

US-009 Checkpoint Contract

US-010 Checkpoint Manager

US-011 Checkpoint Resolver

US-012 Resume Dispatcher

---

### Phase 6 - Execution Engine

US-023 Parallel Execution Engine

US-024 Timeout Framework

US-022 Sequential Execution

---

### Phase 7 - Execution Integration

US-025 Execution Engine Integration

US-019 Artifact Pipeline

---

### Phase 8 - Notifications

US-029 Notification Framework

---

### Phase 9 - AWX

US-034 Job Templates

US-035 Sequential Workflow

US-036 Parallel Workflow

US-037 Resume Workflow

# Add New Section After 1C

## 1D. Review Requirements

| Jira | Review Required |
|--------|--------|
| US-001 | No |
| US-002 | No |
| US-003 | Yes |
| US-004 | No |
| US-005 | No |
| US-006 | Yes |
| US-007 | No |
| US-002A | Yes |
| US-040 | Yes |
| US-013 | No |
| US-014 | No |
| US-015 | No |
| US-016 | No |
| US-017 | No |
| US-018 | Yes |
| US-019 | Yes |
| US-020 | No |
| US-021 | No |
| US-022 | Yes |
| US-023 | Yes |
| US-024 | Yes |
| US-025 | Yes |
| US-026 | Yes |
| US-027 | Yes |
| US-028A | No |
| US-029 | Yes |
| US-034 | No |
| US-035 | Yes |
| US-036 | Yes |
| US-037 | Yes |
| US-009 | Yes |
| US-010 | Yes |
| US-011 | Yes |
| US-012 | Yes |



I plan to use below prompts for execution

1. Design prompt (gpt-oss) 

Implement <JIRA_ID>.

Use Volume 6 Jira Requirements Mapping Matrix as the primary navigation document.

Instructions:

1. Read the documents listed for <JIRA_ID> in the Jira Requirements Mapping Matrix.
2. Use Volume 6 as the authoritative source for:
   - scope
   - impacted components
   - contracts
   - test scope
3. Follow:
   - Technical Specification
   - Data Contracts
   - Coding Standards
4. Do not infer requirements outside the specifications.
5. Do not generate code.

Produce:

1. Jira Understanding
   - objective
   - acceptance criteria
   - impacted components

2. Design
   - approach
   - sequence flow
   - component interactions

3. File Impact Analysis
   - files to create
   - files to modify

4. Contract Analysis
   - contracts consumed
   - contracts produced

5. Error Handling Strategy

6. Testing Strategy
   - unit tests
   - integration tests

7. Risks and Assumptions

Identify:
- ambiguities
- missing specifications
- conflicting requirements

Wait for approval before generating code.

2. Implementation Prompt (qwen-coder-next) 
 
Implement approved design for <JIRA_ID>.

Use Volume 6 Jira Requirements Mapping Matrix as the primary navigation document.

Instructions:

1. Read the documents listed for <JIRA_ID>.
2. Follow:
   - Technical Specification
   - Data Contracts
   - Coding Standards
   - Approved Design

3. Modify only:
   - Roles listed in Volume 6
   - Playbooks listed in Volume 6
   - Schemas listed in Volume 6
   - Tests required by Volume 6

4. Do not modify components outside the Jira scope.

5. Follow all contracts exactly.
6. Follow all architectural constraints exactly.
7. Follow all ADRs exactly.

Generate:

1. Production Code
2. Unit Tests
3. Integration Tests (if required by Volume 6)
4. README Updates
5. Example Configuration (if applicable)

Provide:

1. Files Created
2. Files Modified
3. Contract Compliance Summary
4. Test Coverage Summary
5. Assumptions Made

Highlight any specification gaps instead of inventing behavior.

3. Review prompt (gpt-oss) 

Review implementation of <JIRA_ID>.

Use Volume 6 Jira Requirements Mapping Matrix as the primary navigation document.

Validate implementation against:

- Technical Specification
- Data Contracts
- Coding Standards
- Approved Design
- All documents referenced for <JIRA_ID> in Volume 6

Review Areas:

1. Scope Compliance
   - Were unrelated files modified?
   - Were required files missed?

2. Architectural Compliance
   - ADR compliance
   - Component responsibility compliance
   - Component interaction compliance
   - Architectural constraint compliance

3. Contract Compliance
   - Input contracts
   - Output contracts
   - Checkpoint contracts
   - Report contracts
   - Notification contracts

4. Coding Standards Compliance
   - Logging
   - Error handling
   - Naming conventions
   - Repository structure

5. Testing Compliance
   - Required unit tests present
   - Required integration tests present
   - Missing edge cases

6. Security Review
   - Hardcoded credentials
   - Hardcoded paths
   - Sensitive data leakage

7. Maintainability Review
   - Complexity concerns
   - Tight coupling
   - Reusability concerns

Produce:

1. Critical Issues
2. Major Issues
3. Minor Issues
4. Missing Requirements
5. Improvement Recommendations

Do not modify code.

Use Ansible Jinja2 template processing.

Rationale:

- Aligns with Ansible best practices.
- Avoids introducing custom Python/Shell preprocessing logic.
- Keeps environment resolution independent of AWX.
- Allows execution from both AWX and standalone ansible-playbook runs.
- Environment variables (env_name, region) shall be supplied from the manifest/execution context and rendered through environment_template.yml.j2.

Implement:
- environment_template.yml.j2
- environment_resolver role
- unit tests
- integration tests

Do not implement a custom template engine or preprocessing script.

