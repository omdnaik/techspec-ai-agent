### .opencode/0_tech_stack.json
**Generator:** Tech Detector Agent
**Purpose:** Sets the parsing rules and ecosystem boundaries for all downstream agents.
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": [
    "language",
    "framework",
    "build_system",
    "di_mechanism",
    "config_patterns"
  ],
  "properties": {
    "language": {
      "type": "string",
      "enum": ["JAVA", "KOTLIN", "JAVASCRIPT", "TYPESCRIPT", "GO", "PYTHON"]
    },
    "framework": {
      "type": "string",
      "enum": ["SPRING_BOOT", "QUARKUS", "MICRONAUT", "VANILLA", "EXPRESS", "NESTJS"]
    },
    "build_system": {
      "type": "string",
      "enum": ["MAVEN", "GRADLE", "NPM", "GO_MOD", "PIP"]
    },
    "di_mechanism": {
      "type": "string",
      "enum": ["SPRING_ANNOTATIONS", "JAKARTA_CDI", "NEST_INJECTION", "MANUAL_WIRING", "NONE"]
    },
    "config_patterns": {
      "type": "array",
      "items": { "type": "string" },
      "description": "File extensions for config resolution (e.g., ['*.yml', '*.properties', '*.env'])"
    }
  }
}

```
### .opencode/1_business_scope.json
**Generator:** Jira & FSD Translator Agent
**Purpose:** Fuses Jira ticket data with the deterministic state-machine extraction of the FSD PDF.
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": [
    "jira_ticket_id",
    "parsed_fsd_files",
    "technical_translation"
  ],
  "properties": {
    "jira_ticket_id": { "type": "string" },
    "parsed_fsd_files": {
      "type": "array",
      "items": { "type": "string" },
      "description": "List of FSDs successfully parsed by the deterministic state-machine tool."
    },
    "technical_translation": {
      "type": "object",
      "properties": {
        "core_business_domains": {
          "type": "array",
          "items": { "type": "string" }
        },
        "bash_search_terms": {
          "type": "array",
          "items": { "type": "string" },
          "description": "Target nodes/symbols for Depwire to index."
        },
        "extracted_business_rules": {
          "type": "array",
          "items": { "type": "string" },
          "description": "Rules explicitly extracted from the FSD template."
        },
        "database_tables_mentioned": {
          "type": "array",
          "items": { "type": "string" }
        },
        "api_endpoints_mentioned": {
          "type": "array",
          "items": { "type": "string" }
        }
      }
    }
  }
}

```
### .opencode/3a_code_impact_tree.json
**Generator:** Guided Explorer Agent
**Purpose:** The definitive mapping of Java layer logic, integrating AST-extracted source code, polymorphic resolutions, and segregated test boundaries.
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["code_changes", "impacted_tests"],
  "properties": {
    "code_changes": {
      "type": "array",
      "items": {
        "type": "object",
        "required": [
          "file_path",
          "class_name",
          "pattern_type",
          "impacted_methods"
        ],
        "properties": {
          "file_path": { "type": "string" },
          "class_name": { "type": "string" },
          "pattern_type": {
            "type": "string",
            "enum": ["LINEAR_SERVICE", "STRATEGY_INTERFACE", "STRATEGY_IMPLEMENTATION", "FACTORY", "JPA_ENTITY"]
          },
          "resolved_bean_name": {
            "type": ["string", "null"],
            "description": "The exact Spring Qualifier or CDI Named identifier resolved during polymorphic trace."
          },
          "impacted_methods": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "method_name": { "type": "string" },
                "extracted_source_code": {
                  "type": "string",
                  "description": "The EXACT raw source code block extracted via the Tree-sitter AST tool."
                },
                "context_truncated": {
                  "type": "boolean",
                  "description": "True if the God-Class fail-safe was triggered (exceeded 150 lines)."
                },
                "summary_of_logic": { "type": "string" },
                "downstream_invocations": {
                  "type": "array",
                  "items": { "type": "string" },
                  "description": "Immediate depth=1 callees (e.g., 'TaxRepository.save')."
                }
              }
            }
          }
        }
      }
    },
    "impacted_tests": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "test_file_path": { "type": "string" },
          "test_class_name": { "type": "string" },
          "verified_by_caller_node": { "type": "string" }
        }
      }
    }
  }
}

```
### .opencode/3b_config_impacts.json
**Generator:** Config & SQL Scout
**Purpose:** Captures non-AST configurations and guarantees full-context SQL schema extractions for JPA reconciliation.
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["config_changes", "database_migrations"],
  "properties": {
    "config_changes": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "file_path": { "type": "string" },
          "key": { "type": "string" },
          "current_value": { "type": ["string", "null"] },
          "impact_summary": { "type": "string" }
        }
      }
    },
    "database_migrations": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["script_file_path", "target_table", "full_sql_block"],
        "properties": {
          "script_file_path": { "type": "string" },
          "statement_type": { 
            "type": "string", 
            "enum": ["CREATE_TABLE", "ALTER_TABLE", "INSERT", "UPDATE", "STORED_PROCEDURE"] 
          },
          "target_table": { "type": "string" },
          "full_sql_block": {
            "type": "string",
            "description": "The complete, multi-line DDL/DML context block extracted from the script."
          }
        }
      }
    }
  }
}

```
### Coding_Agent_Manifest.json
**Generator:** Report Agent (Dual-Output Phase)
**Purpose:** The strict, machine-readable execution contract fed directly to the downstream autonomous coding agent.
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["jira_ticket_id", "execution_plan"],
  "properties": {
    "jira_ticket_id": { "type": "string" },
    "execution_plan": {
      "type": "array",
      "items": {
        "type": "object",
        "required": [
          "file_to_modify",
          "action",
          "required_logic_changes"
        ],
        "properties": {
          "file_to_modify": { "type": "string" },
          "action": {
            "type": "string",
            "enum": ["UPDATE_METHOD", "ADD_METHOD", "CREATE_FILE", "ADD_TEST_CASE", "UPDATE_CONFIG", "WRITE_SQL_MIGRATION"]
          },
          "target_method_signature": {
            "type": ["string", "null"]
          },
          "target_extracted_source_code": {
            "type": ["string", "null"],
            "description": "The exact baseline code the agent must modify, provided by the AST tool."
          },
          "required_logic_changes": {
            "type": "string",
            "description": "Explicit instructions grounded strictly in the FSD and graph trace."
          },
          "dependencies_to_inject": {
            "type": "array",
            "items": { "type": "string" }
          },
          "data_layer_reconciliation_warning": {
            "type": ["string", "null"],
            "description": "Specific naming mismatches between JPA and SQL the agent must fix."
          }
        }
      }
    }
  }
}

```
