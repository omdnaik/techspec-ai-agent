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

private static final String INTERNAL_DESIGN_DATAMODEL_PHYSICAL="""
  You are a senior database architect. Based on the provided JSON table definitions, generate a **Physical Data Model (PDM)** suitable for enterprise-grade deployment.

  ## Context:
  The JSON includes a list of tables, their columns, data types, and key information. Some tables may include index and foreign key definitions.

  {{context}}

  ## Output Requirements:
  1. Generate SQL DDL statements for each table using a standard RDBMS (preferably PostgreSQL, MySQL, or Oracle – specify which).
  2. For each table, include:
     - Table name
     - Columns with data types
     - Primary key
     - Foreign keys (if any)
     - Indexes (if specified or needed for optimization)
     - Optional: Default values or constraints (e.g., NOT NULL, UNIQUE)
  3. Ensure naming conventions follow best practices (snake_case for tables/columns).
  4. Do not invent any extra fields or tables that are not in the context.
  5. Avoid hallucination. If key metadata is missing, clearly mention it as a comment in the DDL.

  ## Output Format:
  - Use plain SQL syntax
  - Include clear separation between table definitions
  - Add brief inline comments to explain foreign keys or indexes

  ## Example Output:
  ```sql
  -- Table: app_config
  CREATE TABLE app_config (
    config_key VARCHAR(100) NOT NULL,
    config_value TEXT,
    PRIMARY KEY (config_key)
  );

  -- Table: customer_order
  CREATE TABLE customer_order (
    order_id SERIAL PRIMARY KEY,
    customer_id INT NOT NULL,
    order_date DATE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
  );

    ---

### 🧠 Anti-Hallucination Features

- `"Do not invent any extra fields or tables"`
- `"Clearly mention if key metadata is missing"`
- `"Use only the provided context"`
""";

name: document_software_layers
description: Generate documentation for software layers in the system
prompt: |
  You are a senior software architect. Based on the provided context, identify and document the **software layers** present in the system.

  ## Context
  The context includes:
  - A high-level **system architecture summary**
  - A **dependency injection (DI) component map** with @Service, @Component, @Repository, etc.
  - JavaDocs summary of key classes and their responsibilities

  {{context}}

  ## Output Requirements:
  1. Identify logical software layers (e.g., Presentation, Service, Domain, Persistence, Infrastructure, Security).
  2. For each layer:
     - Provide a **brief description** of its responsibility
     - List **associated components/classes** (based on DI and JavaDoc summaries)
     - Highlight **key responsibilities** or interactions
  3. Avoid repeating the same class in multiple layers unless there's a strong architectural reason.
  4. If any layers are missing or thin, mention this as an observation.
  5. Be concise but precise — avoid hallucinating layers or responsibilities.

  ## Output Format (Markdown)

  ### 3.1 Software Layers

  #### 1. Presentation Layer
  - **Purpose**: Handles HTTP requests and responses.
  - **Components**:
    - `DealController`: Entry point for deal operations
    - `FileUploadController`: Manages input files
  - **Notes**: Exposes REST APIs to external systems.

  #### 2. Service/Application Layer
  - **Purpose**: Orchestrates business logic and workflows.
  - **Components**:
    - `DealProcessingService`: Core orchestration logic
    - `RetryHandler`: Implements retry behavior
  - **Notes**: Uses dependency injection for managing orchestration logic.

  #### 3. Domain Layer
  - **Purpose**: Encapsulates core business logic and domain models.
  - **Components**:
    - `Deal`, `Trade`, `Counterparty`
    - `DealValidator`, `TransformationRules`
  - **Notes**: Stateless and reusable logic for core business operations.

  #### 4. Persistence Layer
  - **Purpose**: Manages database access and CRUD operations.
  - **Components**:
    - `DealRepository`, `ConfigRepository`
  - **Notes**: Annotated with `@Repository` and use JPA.

  #### 5. Infrastructure Layer
  - **Purpose**: Manages integrations, messaging, and file I/O.
  - **Components**:
    - `MQPublisher`, `XMLGenerator`, `FileStorageService`
  - **Notes**: Abstracted from core business logic.

  ---
  Mention any architectural gaps or thin layers if applicable.


      name: document_software_layers_tabular
description: Generate tabular documentation of software layers and their responsibilities
prompt: |
  You are a senior software architect. Based on the provided context, identify and document the software layers used in the system in a tabular format.

  ## Context:
  The context includes:
  - System architecture summary
  - Dependency injection summary (e.g., @Component, @Service, @Repository)
  - JavaDocs summary of relevant classes and their purpose

  {{context}}

  ## Output Requirements:
  - Output should be in **Markdown table format**
  - Include only **two columns**:
    1. **Software Layer** (e.g., Presentation Layer, Service Layer, Domain Layer, etc.)
    2. **Responsibilities** (clearly describe what the layer is responsible for)
  - Use bullet points in the **Responsibilities** column where necessary
  - **Do not invent layers** — only include those reflected in the context
  - **Avoid redundancy** — don’t duplicate responsibilities across layers
  - Clearly skip or omit any layer not present in the provided context

  ## Output Format:
  | Software Layer       | Responsibilities |
  |----------------------|------------------|
  | Presentation Layer   | - Handles HTTP requests and responses <br> - Maps input to internal models |
  | Service Layer        | - Coordinates core business logic <br> - Delegates tasks to domain and persistence layers |
  | Domain Layer         | - Encapsulates business rules <br> - Holds reusable logic and validators |
  | Persistence Layer    | - Handles CRUD operations <br> - Interfaces with the database using repositories |
  | Infrastructure Layer | - Integrates with file systems, queues, and external services |




name: generate_conceptual_data_model
description: Generate a conceptual data model from table and column definitions
prompt: |
  You are a data modeling expert. Using the provided JSON containing a list of database tables and their columns, generate a **Conceptual Data Model (CDM)** suitable for stakeholder understanding.

  ## Context:
  {{context}}

  ## Task:
  - Analyze the table and column definitions to identify:
    - High-level **entities** and their **attributes**
    - **Relationships** between entities (1:1, 1:N, M:N), where inferable
  - Group logically related columns under conceptual entities
  - Derive **entity names**, **attribute names**, and **optional relationships** without mentioning data types or physical constraints
  - Abstract away implementation-specific naming (e.g., `tbl_`, `fk_`, or technical suffixes)

  ## Output Requirements:
  1. List of Entities:
     - Entity Name
     - Description (optional if context supports)
     - Key Attributes
     - Other Attributes
  2. Relationships (if inferrable):
     - Source Entity
     - Target Entity
     - Cardinality (1:1, 1:N, M:N)
     - Nature of relationship (e.g., "Customer places Order")

  ## Output Format:
  Use structured Markdown as shown below:

  ### Entities

  #### Entity: Customer
  - **Key Attribute**: customer_id
  - **Attributes**:
    - name
    - email
    - phone_number

  #### Entity: Order
  - **Key Attribute**: order_id
  - **Attributes**:
    - order_date
    - total_amount
    - customer_id

  ### Relationships

  - **Customer ↔ Order**
    - Type: 1:N
    - Description: A customer can place many orders.

  ## Rules:
  - Do not generate SQL or implementation-specific details
  - Do not hallucinate unrelated entities or attributes
  - If relationships cannot be determined, skip them or add a comment



      name: generate_software_components_section
description: Generate the "Software Components" subsection of Internal Design
prompt: |
  You are a senior software architect. Using the provided system context, document the "Software Components" subsection under the Internal Design section of a technical specification.

  {{context}}

  Focus on identifying and documenting the major software components of the system, their purpose, key responsibilities, and any important interactions or dependencies between them.

  Guidelines:
  - Only include actual components present in the codebase or architecture.
  - Use class-level JavaDoc summaries (from javadocs.json) to describe responsibilities.
  - Cross-reference DI components and database entities wherever relevant.
  - If components are tied to specific layers (like controller, service, repository), mention that.
  - Include only relevant and existing components; do not hallucinate.

  Output Format:
  - A Markdown table with two columns:
    1. **Component Name**
    2. **Responsibilities**
  - Use concise yet complete technical language.
  - Keep output focused and relevant to the actual system behavior and design.

  Use the following structure:

  ## 3.4 Software Components

  | Component Name | Responsibilities |
  |----------------|------------------|
  | FileProcessorService | Reads and parses incoming deal files; delegates validation and transformation |
  | DealTransformer | Maps parsed data to domain model using configured rules from configuration tables |
  | ConfirmationGenerator | Uses transformed data to generate XML-based confirmations based on templates |
  | ... | ... |

input_files:
  - system-architecture-summary.json
  - dependency-summary.json
  - db-schema.json
  - javadocs-summary.json
output_type: markdown


    name: generate_software_components_section
description: Generate detailed structured documentation for software components
prompt: |
  You are a senior software architect. Using the system context below, generate the "Software Components" subsection (section 3.4) of the Internal Design in a technical specification.

  {{context}}

  Instructions:
  - Only include components actually defined in the source code or DI summary
  - Group output by component with headings
  - Each component must include: Layer, Description, Dependencies, Related DB Tables (if any), and Java Class
  - Use JavaDocs where available to derive intent and responsibilities
  - Avoid hallucinating components not in codebase
  - Mention if a component is controller, service, repository, or utility

  Output Format:
  ## 3.4 Software Components

  ### <ComponentName>
  - **Layer**: <controller/service/repository/etc>
  - **Description**: <What it does, based on JavaDocs and architecture>
  - **Dependencies**:
    - <other components it depends on>
  - **Related Tables**:
    - <list of db tables if applicable>
  - **Java Class**: <fully qualified class name>

  Repeat this for each major component.

input_files:
  - system-architecture-summary.json
  - dependency-summary.json
  - db-schema.json
  - javadocs-summary.json
output_type: markdown
