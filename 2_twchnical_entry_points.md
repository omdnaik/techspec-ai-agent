{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["verified_entry_points"],
  "properties": {
    "verified_entry_points": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["file_path", "class_name", "matched_term", "layer"],
        "properties": {
          "file_path": {
            "type": "string",
            "description": "The exact relative path from the repository root (e.g., 'src/main/java/com/app/tax/TaxService.java')."
          },
          "class_name": {
            "type": "string",
            "description": "The fully qualified class name or simple name."
          },
          "matched_term": {
            "type": "string",
            "description": "The specific search term from Phase 1 that resulted in this file being discovered."
          },
          "layer": {
            "type": "string",
            "description": "The architectural layer this file belongs to (e.g., 'Service')."
          }
        }
      }
    }
  }
}
