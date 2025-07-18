package com.techspec.agent.prompt;

public class PromptTemplates {

    public static final String MASTER_TECH_SPEC = """
        You are a senior software architect. Using the below interface, database, DI, config metadata and transformation details, generate a well-structured master technical specification.

        {{context}}

         Follow this Table of Contents structure:
           1. INTRODUCTION
            1.1 Purpose Overview
            1.2 Scope
            1.3 Glossary
            1.4 References
            1.5 Compliance of the Technologies/Products used with CIB norms

          2. SYSTEM OVERALL VIEW
            2.1 Application Architecture
            2.2 External Interfaces

          3. INTERNAL SYSTEM DESIGN
            3.1 Software Layers
            3.2 Major Internal Interfaces
            3.3 Data Model
            3.4 Software Component
            3.5 Automation Process
            3.6 External Files
            3.7 Reports

          4. ERROR MESSAGES
            4.1 Error Message Management
            4.2 Error Message Typologies
            4.3 Log Management

          5. SECURITY DESIGN
          6. JURIDICTION

        Output Requirements:
        - Use professional technical language
        - Use bullet points or tables where applicable (e.g., for error codes, field-level specs)
        - Ensure each subsection is populated with concise but complete technical details relevant to interface design and behavior
        - Do not invent irrelevant features; keep everything grounded in plausible enterprise software design
        - Format the output in clean, professional Markdown.
    """;

    public static final String INTERFACE_SCHEMA = """
        You are an integration expert. Based on the below controller/messaging endpoints, generate detailed interface schemas including request/response models.

        {{context}}

        Provide:
        - Interface name
        - Operation type
        - Input fields with type and validations
        - Output fields
        - Sample payloads

        Output Requirements:
        - Use professional technical language
        - Use bullet points or tables where applicable (e.g., for error codes, field-level specs)
        - Ensure each subsection is populated with concise but complete technical details relevant to interface design and behavior
        - Do not invent irrelevant features; keep everything grounded in plausible enterprise software design
        - Format the result in Markdown.
    """;

    public static final String TRANSFORMATION_TRACE = """
        Analyze the following source-to-target mapping logic and identify transformation rules for generating confirmation/advice messages from transaction DTOs.

        {{context}}

        Produce:
        - Source fields
        - Target fields
        - Logic used (direct copy, conversion, lookup, static assignment)

        Present the result in a transformation trace table (Markdown).
    """;
}

public static final String SYSTEM_OVERVIEW_PURPOSE_SCOPE = """
As an experienced Solution Architect, you are documenting the System Overview of a Spring Boot Java-based file processing system.
Focus on robustness in terms of maintainability, modular design, scalability, and performance.

---

CONTEXT
{{context}}

The provided context may include:
- System architecture summary
- JavaDoc summary
- DI metadata
- DB schema/config tables

---

DOCUMENTATION GOALS

1. Purpose Overview
- What does the system do? What business problem does it solve?
- Focus on robustness, maintainability, and modularity.

2. Scope
- What functionality or modules are covered?
- Provide a high-level summary only of modules and key features.
- Avoid listing the same feature under multiple headings.

Include:
- Technology Stack
- Key Features (e.g., asynchronous processing, file validation, XML generation, DB interactions)
- Modules (e.g., File parser, Deal processor, XML generator, DB interface)
- Responsibilities of each module

---

ANTI-REDUNDANCY GUARDRAILS
- Do not repeat features or modules across multiple sections unless new context is added.
- If a module is listed in "Modules", do not repeat its features under "Functionality" unless strictly required.
- Use concise bullet-point format. Avoid verbose or repetitive explanations.
- Group similar information logically under one section.

---

OUTPUT FORMAT
- Use clean markdown with bullet points.
- Structure your response into the following sections:
  - Purpose Overview
  - Scope
    - Technology Stack
    - Key Features
    - Modules
    - Responsibilities

Only include information that is clearly verifiable from the context. Do not fabricate system features or assumptions.
Return markdown content only.
""";
