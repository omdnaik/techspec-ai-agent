# Coding Standards

## Purpose

Defines mandatory coding standards for all generated code.

---

# General Principles

Code SHALL be:

```text
Readable
Maintainable
Testable
Modular
Idempotent
```

---

# Repository Standards

Repository structure must follow Volume 1.

No additional top-level directories permitted without architectural approval.

---

# Python Standards

## Version

```text
Python 3.11+
```

## Import Ordering

```python
# Standard Library

# Third Party

# Local Imports
```

## Logging

Allowed:

```python
logger.info()
logger.warning()
logger.error()
```

Not Allowed:

```python
print()
```

## Exception Handling

Good:

```python
try:
    ...
except Exception as ex:
    logger.error(...)
    raise
```

Bad:

```python
except:
    pass
```

---

# Ansible Standards

## Task Names Mandatory

Good:

```yaml
- name: Validate manifest schema
```

Bad:

```yaml
- debug:
```

## Variable Naming

Allowed:

```yaml
artifact_timeout_minutes
```

Not Allowed:

```yaml
artifactTimeoutMinutes
```

## Role Structure

```text
defaults/
vars/
tasks/
handlers/
README.md
```

## Playbook Rules

Playbooks orchestrate only.

Business logic belongs in roles.

---

# Error Handling Standards

Every failure must:

```text
Classify
Log
Checkpoint
Return structured result
```

---

# Logging Standards

```text
[TIMESTAMP]
[RUN_ID]
[ARTIFACT]
[ROLE]
[MESSAGE]
```

Example:

```text
2025-05-18T10:15:00Z
RUN_001
payment-service
deploy
Deployment started
```

---

# Testing Standards

## Unit Tests

Required:

```text
Success Test
Failure Test
Edge Case Test
```

## Integration Tests

Required:

```text
Parallel Execution
Sequential Execution
Timeout
Resume
Reporting
```

---

# Documentation Standards

Every role must document:

```text
Purpose
Inputs
Outputs
Failure Conditions
Examples
```

README required.

---

# Security Standards

Never store:

```text
Passwords
Tokens
SSH Keys
```

In:

```text
Git
group_vars
Reports
Checkpoints
```

Use:

```text
AWX Credentials
```

---

# Review Checklist

Verify:

```text
No hardcoded paths
No hardcoded credentials
Contract compliance
Logging compliance
Checkpoint compliance
Test coverage
```

---

# AI Agent Restrictions

Agents SHALL NOT:

```text
Invent schemas
Invent status values
Invent failure types
Invent directory structures
```

Agents SHALL follow:

```text
Technical Specification
Volumes 1-7
Data Contracts
Coding Standards
```

---

# Definition of Done

Implementation complete only when:

```text
Code Generated
Tests Generated
Tests Passing
Documentation Updated
Contract Compliance Verified
```
