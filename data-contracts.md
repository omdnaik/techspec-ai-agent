# Data Contracts

## Purpose

Defines all data contracts exchanged between framework components.

Manifest
The manifest is the external JSON representation of the Execution Context Contract. It contains the user-supplied execution request, including environment, target host, execution mode, and artifact definitions. After validation, it is transformed into an internal Execution Context used by the execution engine.



---

## Execution Context Contract

```yaml
execution_context:
  run_id:
  environment:
  target_host:
  execution_mode:
  artifact_id:
  artifact_name:
  artifact_workspace:
  artifact_report_directory:
  artifact_checkpoint_directory:
  timeout_minutes:
```

Producer:
- Execution Engine

Consumers:
- All Lifecycle Roles

---

## Role Result Contract

```yaml
role_result:
  status:
  phase:
  start_time:
  end_time:
  duration_seconds:
  error_message:
```

Allowed Status Values:

```text
SUCCESS
FAILED
SKIPPED
TIMEOUT
```

---

## Artifact Definition Contract

```json
{
  "artifactId": "",
  "artifactName": "",
  "artifactType": "",
  "deploymentRequired": true,
  "deployment": {},
  "execution": {},
  "validation": {}
}
```

---

## Artifact Pipeline Contract

```yaml
artifact_pipeline:
  cleanup:
  precheck:
  deploy:
  sanity:
  execute:
  validate:
  housekeeping:
```

Each phase returns:

```yaml
role_result
```

---

## Worker Contract

```yaml
worker:
  worker_id:
  assigned_artifact:
  status:
  start_time:
  end_time:
```

---

## Checkpoint Contract

Checkpoint File:

```text
artifact.completed
```

Checkpoint Content:

```properties
artifactName=payment-service
status=COMPLETED
timestamp=2025-05-18T10:15:00Z
```

Allowed States:

```text
STARTED
COMPLETED
FAILED
TIMEOUT
SKIPPED
```

---

## Artifact Report Contract

```json
{
  "runId": "",
  "artifactId": "",
  "artifactName": "",
  "status": "",
  "failureType": "",
  "startTime": "",
  "endTime": "",
  "durationSeconds": 0
}
```

---

## Validation Report Contract

```json
{
  "artifactId": "",
  "artifactName": "",
  "validationStatus": "",
  "differenceCount": 0
}
```

---

## Execution Summary Contract

```json
{
  "runId": "",
  "overallStatus": "",
  "totalArtifacts": 0,
  "successfulArtifacts": 0,
  "failedArtifacts": 0,
  "timedOutArtifacts": 0,
  "durationSeconds": 0
}
```

---

## Failure Contract

```json
{
  "failureType": "",
  "failureMessage": "",
  "artifactName": "",
  "timestamp": ""
}
```

Allowed Failure Types:

```text
CONFIGURATION_FAILURE
MANIFEST_FAILURE
HOST_VALIDATION_FAILURE
DOWNLOAD_FAILURE
DEPLOYMENT_FAILURE
SANITY_FAILURE
EXECUTION_FAILURE
VALIDATION_FAILURE
TIMEOUT_FAILURE
```

---

## Notification Contract

```json
{
  "runId": "",
  "overallStatus": "",
  "totalArtifacts": 0,
  "successfulArtifacts": 0,
  "failedArtifacts": 0
}
```

---

## Timestamp Contract

Format:

```text
ISO-8601 UTC
```

Example:

```text
2025-05-18T10:15:00Z
```

---

## Contract Ownership

| Contract | Owner |
|-----------|--------|
| Execution Context | Execution Engine |
| Role Result | All Roles |
| Worker | Execution Engine |
| Checkpoint | Checkpoint Framework |
| Artifact Report | Reporting Framework |
| Validation Report | Validation Framework |
| Execution Summary | Reporting Framework |
| Notification | Notification Framework |
