{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["ticket_id", "core_domains", "bash_search_terms", "target_layers"],
  "properties": {
    "ticket_id": {
      "type": "string",
      "description": "The original Jira ticket ID (e.g., PROJ-123)."
    },
    "core_domains": {
      "type": "array",
      "items": { "type": "string" },
      "description": "The high-level business nouns extracted from the ticket (e.g., 'Checkout', 'TaxCalculation')."
    },
    "bash_search_terms": {
      "type": "array",
      "items": { "type": "string" },
      "description": "Specific string literals, variable names, or keywords the Scout should grep for in the local repository."
    },
    "target_layers": {
      "type": "array",
      "items": {
        "type": "string",
        "enum": ["Controller", "Service", "Repository", "Entity", "Configuration", "DTO"]
      },
      "description": "The anticipated Spring Boot architectural layers impacted by this domain change."
    }
  }
}
