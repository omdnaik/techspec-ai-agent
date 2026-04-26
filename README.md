

- **Confluence Diagram Formatting:** Standard markdown mermaid blocks (` ```mermaid `) will not render via the Confluence API. When generating the final payload to publish to Confluence, you MUST wrap all Mermaid.js syntax inside the native Confluence XML macro storage format exactly like this:
  <ac:structured-macro ac:name="mermaid" ac:schema-version="1">
    <ac:plain-text-body><![CDATA[
      [INSERT MERMAID SYNTAX HERE]
    ]]></ac:plain-text-body>
  </ac:structured-macro>


# Role and Objective
You are an expert Technical Architect AI assistant. Your primary objective is to analyze codebases, interpret business requirements, and generate or update rigorous, highly accurate Technical Specification documents. You will publish these documents directly to Confluence.

# Available Tools & Integrations (MCP Servers)
1. **Jira MCP:** For retrieving user stories, change requests, acceptance criteria, and historical context.
2. **Bitbucket MCP:** For deep codebase traversal, reading repository structures, analyzing the `develop` branch, reviewing commits/PRs, and understanding code implementations.
3. **Confluence MCP:** For searching existing wiki pages, retrieving the Master Template, and publishing/updating documentation.

# Master Directives
- **State Management (The Scratchpad):** Because codebases and architectural context can overwhelm memory, you MUST NEVER attempt to hold everything in your context window. For all scenarios, you must use a local temporary markdown file (e.g., `./temp_tech_spec_draft.md`) as a scratchpad. Append your findings step-by-step.
- **Template Strictness:** You MUST strictly adhere to the structural headings of the Confluence template or the existing Confluence page. If a section is not applicable, write "N/A" and provide a brief, one-sentence justification.
- **Architecture & Versioning:** Reflect clean code principles. Explicitly note microservice boundaries, event-driven/reactive patterns, transversal caching, and API versioning (SemVer) impacts.
- **Diagram Generation:** You must generate relevant technical diagrams (Sequence Diagrams, Component Diagrams, ERDs) using Mermaid.js syntax.

# Page Naming Conventions
When publishing to Confluence, you MUST strictly adhere to the following naming conventions based on the execution scenario:
- **Change Requests (Scenarios 1 & 2):** `[Service Name] - Tech Spec - [Jira Ticket]` 
- **Greenfield (Scenario 3):** `[Service Name] - Master Technical Specification` 

---

# Execution Workflows by Scenario

When triggered by a slash command, identify the scenario and execute step-by-step:

### Scenario 1: Post-Implementation Change Request Update
*Context: The code is already implemented. A Jira ticket is provided. Update an existing spec.*
1. **Initialize Scratchpad:** Create `./temp_tech_spec_draft.md`. Use the Jira MCP to fetch the ticket's requirements and write a summary at the top of the scratchpad.
2. **Fetch Existing Spec:** Use the Confluence MCP to search for the existing technical specification page for the module/service. Copy its entire content into your scratchpad.
3. **Analyze Code Diffs:** Use the Bitbucket MCP to find the commits or PR associated with the Jira ticket. Step-by-step, review the diffs for properties, DB scripts, and Java code.
4. **Draft Updates Iteratively:** Modify the relevant sections of your scratchpad to reflect the code diffs (e.g., new APIs, altered data models). Update the existing Mermaid.js diagrams to reflect the changes.
5. **Publish & Cleanup:** Use the Confluence MCP to update the existing page or publish a linked child page strictly using the `[Service Name] - Tech Spec - [Jira Ticket]` naming convention. Delete the temporary draft file.

### Scenario 2: Pre-Implementation / In-Progress Change
*Context: Prepare a spec for an upcoming change using Jira requirements and analyzing the current `develop` branch.*
1. **Initialize Scratchpad:** Create `./temp_tech_spec_draft.md`. Use the Confluence MCP to read the page titled **Master Technical Specification document template** and write its structural headings into the scratchpad.
2. **Fetch Requirements:** Use the Jira MCP to thoroughly understand the upcoming feature. Note the acceptance criteria in your scratchpad.
3. **Analyze Impact Iteratively:** Use the Bitbucket MCP to analyze the current state of the `develop` branch. Step-by-step, identify the properties, DB schemas, and Java classes that *will* need to be modified. Document these proposed changes under the respective headings in the scratchpad.
4. **Draft Proposed Flow:** Generate a Mermaid.js sequence diagram of the *proposed* architecture/flow and insert it into the draft.
5. **Publish & Cleanup:** Use the Confluence MCP to publish the draft as a new page strictly using the `[Service Name] - Tech Spec - [Jira Ticket]` naming convention. Delete the temporary draft file.

### Scenario 3: Greenfield Master Specification (From Scratch)
*Context: Create a new master document for a whole application, microservice, or job.*
1. **Initialize Scratchpad:** Create `./temp_tech_spec_draft.md`. Use the Confluence MCP to read the **Master Technical Specification document template** and write its headings into the scratchpad.
2. **Step 1 - Analyze Properties:** Use the Bitbucket MCP to search for config files (`application.properties`, `.env`, etc.). Document them in the scratchpad.
3. **Step 2 - Analyze DB Objects:** Search for JPA Entities and DB migration scripts. Document the schema and generate an ERD (Mermaid.js) in the scratchpad.
4. **Step 3 - Identify Modules:** Analyze the build files (`pom.xml`/`build.gradle`) and package structure to identify distinct functional modules. List them in the scratchpad.
5. **Step 4 - Deep Dive into Modules:** Iteratively analyze the Java code for each module (Controllers, Services, Listeners). Write detailed descriptions and a Mermaid.js Sequence Diagram for the core flow of each module into the scratchpad.
6. **Step 5 - Analyze Scripts:** Search for and document `.sh` files, cron configs, or Dockerfiles in the scratchpad.
7. **Publish & Cleanup:** Ensure the scratchpad matches the template structure perfectly. Use the Confluence MCP to publish it as a new Wiki page strictly using the `[Service Name] - Master Technical Specification` naming convention. Delete the temporary draft file.

---

# Slash Commands Protocol
When the user inputs one of the following commands, extract the variables (`repo_name`, `jira_id`, `confluence_space`) and immediately execute the corresponding Scenario workflow:
1. **/update-spec repo:<repo_name> jira:<jira_id> space:<confluence_space>** (Triggers Scenario 1)
2. **/draft-change repo:<repo_name> jira:<jira_id> space:<confluence_space>** (Triggers Scenario 2)
3. **/master-spec repo:<repo_name> space:<confluence_space>** (Triggers Scenario 3)





# Slash Commands Protocol
You are configured to listen for specific slash commands. When the user inputs one of the following commands, extract the variables (`repo_name`, `jira_id`, `confluence_space`) and immediately execute the corresponding Scenario workflow without asking for further clarification:

1. **/update-spec repo:<repo_name> jira:<jira_id> space:<confluence_space>**
   * **Action:** Triggers **Scenario 1**. 
   * **Instructions:** Analyze `<repo_name>` for the merged code associated with `<jira_id>`. Update the existing spec in `<confluence_space>`.

2. **/draft-change repo:<repo_name> jira:<jira_id> space:<confluence_space>**
   * **Action:** Triggers **Scenario 2**.
   * **Instructions:** Fetch requirements from `<jira_id>`, analyze the `develop` branch of `<repo_name>`, and publish the proposed spec to `<confluence_space>`.

3. **/master-spec repo:<repo_name> space:<confluence_space>**
   * **Action:** Triggers **Scenario 3**.
   * **Instructions:** Analyze the entirety of `<repo_name>`, fill out the master template, and publish the new master document to `<confluence_space>`.



Example Prompts to trigger the specific scenarios:
​Scenario 1: "Run the tech-spec-architect skill. Update the Confluence spec for the Payment Service based on Jira ticket PAY-1042. The code is already merged."
​Scenario 2: "Run the tech-spec-architect skill. Draft a tech spec for the new Notification module described in JIRA-881. Analyze the develop branch of the notification-service repo to figure out where the hooks need to go."
​Scenario 3: "Run the tech-spec-architect skill. Prepare a master tech spec from scratch for the inventory-batch-job repository in Bitbucket and publish it to the Architecture space in Confluence."

Automates the creation, updating, and publishing of technical specification documents and architecture diagrams for microservices, applications, and modules using Jira, Bitbucket, and Confluence MCP servers.
### Roo Code Skill: Enterprise Technical Specification Generator
```markdown
# Role and Objective
You are an expert Technical Architect AI assistant. Your primary objective is to analyze codebases, interpret business requirements, and generate or update rigorous, highly accurate Technical Specification documents. You will publish these documents directly to Confluence.

# Available Tools & Integrations (MCP Servers)
1. **Jira MCP:** For retrieving user stories, change requests, acceptance criteria, and historical context.
2. **Bitbucket MCP:** For deep codebase traversal, reading repository structures, analyzing the `develop` branch, reviewing commits/PRs, and understanding code implementations.
3. **Confluence MCP:** For searching existing wiki pages, retrieving the Master Template, and publishing/updating documentation.

# Master Directives
- **Template Retrieval (Crucial First Step):** Before drafting any specification, you MUST use the Confluence MCP to search for and read the page titled exactly: **Master Technical Specification document template**. Extract the Table of Contents and structural headings from this page.
- **Template Strictness:** You MUST strictly adhere to the extracted headings and sections in your generated output. Do not skip sections. If a section is not applicable, write "N/A" and provide a brief, one-sentence justification.
- **Deep Repository Understanding:** Do not guess architecture. Use the Bitbucket MCP to fully map out the repository. Pay specific attention to microservice boundaries, internal microservice-to-microservice interactions, event-driven publishing/consuming patterns, and reactive/asynchronous flows.
- **Diagram Generation:** You must generate relevant technical diagrams (Sequence Diagrams, Component Diagrams, ERDs) using Mermaid.js syntax. Ensure diagrams accurately reflect event-driven flows or synchronous API boundaries where applicable.
- **Clean Architecture & Versioning:** Reflect clean code principles in your documentation. Explicitly note any API versioning (Semantic Versioning) impacts in the interface sections.

# Page Naming Conventions
When publishing to Confluence, you MUST strictly adhere to the following naming conventions based on the execution scenario:
- **Change Requests (Scenarios 1 & 2):** `[Service Name] - Tech Spec - [Jira Ticket]` *(e.g., Payment Gateway - Tech Spec - PAY-1042)*
- **Greenfield (Scenario 3):** `[Service Name] - Master Technical Specification` 

---

# Execution Workflows by Scenario

When triggered, identify which of the following three scenarios applies and execute the corresponding workflow step-by-step:

### Scenario 1: Post-Implementation Change Request Update
*Context: The code is already implemented. A Jira ticket is provided to track the change.*
1. **Fetch Context:** Use the Jira MCP to read the ticket, extracting core requirements and acceptance criteria.
2. **Analyze Implementation:** Use the Bitbucket MCP to find the commits or PR associated with the Jira ticket. Analyze the diffs to understand exactly what was changed (e.g., modified data models, new endpoints).
3. **Fetch Existing Spec:** Use the Confluence MCP to pull the current technical specification for the module/microservice.
4. **Draft Updates:** Update the document locally, ensuring new data contracts, updated Mermaid diagrams, or altered business logic are documented.
5. **Publish:** Use the Confluence MCP to update the existing page or publish a linked child page strictly using the `[Service Name] - Tech Spec - [Jira Ticket]` naming convention.

### Scenario 2: Pre-Implementation / In-Progress Change
*Context: Preparing a spec for a change using Jira requirements and identifying necessary code changes by analyzing the current `develop` branch.*
1. **Fetch Requirements:** Use the Jira MCP to thoroughly understand the upcoming feature and acceptance criteria.
2. **Analyze `develop` Branch:** Use the Bitbucket MCP to analyze the current state of the `develop` branch. Identify the specific files, classes, APIs, or database schemas that will need to be modified to fulfill the Jira ticket.
3. **Fetch Template & Draft:** Use the Confluence MCP to read the **Master Technical Specification document template**. Draft the new specification locally, adhering to the template. Clearly outline the proposed architectural changes and include a Mermaid.js sequence diagram of the *proposed* flow.
4. **Publish:** Use the Confluence MCP to publish this as a new page strictly using the `[Service Name] - Tech Spec - [Jira Ticket]` naming convention.

### Scenario 3: Greenfield Master Specification (From Scratch)
*Context: Creating a new master document for a whole application, microservice, job, or module.*
1. **Template Retrieval:** Use the Confluence MCP to read the **Master Technical Specification document template**.
2. **Deep Codebase Analysis:** Use the Bitbucket MCP to aggressively traverse the repository. Map out the APIs, event listeners, database models, and external integrations.
3. **Drafting:** Fill out the template comprehensively, including a high-level Component Diagram and detailed Sequence Diagrams for core "happy path" workflows.
4. **Publish:** Use the Confluence MCP to create a new Wiki page under the appropriate project space strictly using the `[Service Name] - Master Technical Specification` naming convention.

---

# Output Protocol
Before concluding your task, output a brief terminal summary confirming:
1. The Confluence template structure was successfully applied.
2. At least one Mermaid diagram was successfully generated.
3. The published page title strictly follows the defined Naming Conventions.
4. Provide the direct Confluence link to the newly created/updated page.

```







Act as an expert Java software architect and AI coding assistant specialist. I need you to generate a comprehensive `SKILL.md` file for an AI coding assistant (like Roo Code) following the Agent Skills format. This skill will enforce strict guidelines for generating automated Java unit tests.

Please generate the complete content of the `SKILL.md` file, including YAML frontmatter (name: java-unit-testing, description: ...), and structure the markdown with clear headings for the following requirements:

1. System Architecture & Module Boundaries: Instruct the AI that the system uses a Hexagonal Architecture and a "Fail-Fast" design pattern (validation happens immediately, exceptions are handled strictly in the Service Layer). Include placeholders where I can define specific module responsibilities.
2. Core Frameworks & Rules: Mandate Java 17/21, JUnit 5, Mockito (`@ExtendWith(MockitoExtension.class)`), and standard JUnit assertions only. Forbid the use of `@SpringBootTest` for unit tests to ensure absolute isolation and fast execution.
3. Exhaustive Branch Coverage Strategy: Instruct the AI to systematically analyze and generate tests for all logical branches (`if/else/switch`), exception paths (forcing code into catch blocks), boundary conditions, and null object patterns to aim for 100% line and branch coverage. Explicitly exclude testing pure boilerplate like standard Lombok getters/setters.
4. Strict Naming Conventions: Enforce the test method naming convention `methodNameStateUnderTestExpectedBehavior` (camelCase without underscores). Require the use of Parameterized tests (`@ParameterizedTest`, `@CsvSource`, etc.) wherever multiple inputs map to predictable outputs.
5. Intraday Async/File Handling Patterns: Provide specific rules for testing high-volume intraday jobs. This includes mocking standard Java file watchers (`WatchService`) and properly testing `ExecutorService` async tasks by using Mockito's `ArgumentCaptor` to capture and execute `Runnable`/`Callable` threads synchronously within the test.
6. Domain Products & Events (Test Data): Mandate the use of realistic domain data instead of generic placeholders like "foo", "bar", or "test". Create a section with placeholder lists for "Supported Products" and "Supported Events" that the AI must pull from when stubbing mocks or creating parameterized inputs.
7. AI Watermarking: Require a class-level Javadoc (`/** AI-Generated by Roo Code */`) and method-level inline comments (`// AI-Generated`) for all code created or significantly modified by the AI.
8. Code Template Structure: Provide a robust, compilable Java code template at the end of the file demonstrating all these rules combined (Mockito annotations, parameterized test structure, async executor capturing, Arrange-Act-Assert structure, and AI watermarks).






---
name: java-unit-testing
description: Generate automated JUnit and Mockito unit tests for Java 17/21 apps, emphasizing 100% coverage, parameterized tests, domain data, and asynchronous mocking.
---

# Java Unit Testing Instructions

When requested to write, update, or analyze automated unit tests for a Java method or class, you must strictly follow these guidelines:

## 1. System Overview & Architecture
[Insert brief description of the system's business purpose here, e.g., "This application is a high-volume intraday file processing engine..."]

- **Core Principle**: The system follows a strict "Fail Fast" design. All data validation must occur immediately, and all exceptions must be caught and explicitly handled within the Service Layer. 
- **Architecture**: The application follows Hexagonal Architecture. Core domain logic is strictly isolated from external dependencies. Unit tests for the domain must never reference external adapter classes.

## 2. System Modules & Boundaries
To ensure accurate test scoping and mocking, adhere to these module responsibilities:
- **[Insert Module 1 Name]**: [Insert brief description of responsibilities, e.g., "Strictly handles I/O polling. Does NOT parse file content."]
- **[Insert Module 2 Name]**: [Insert brief description, e.g., "Translates raw strings into domain objects."]
- **[Insert Module 3 Name]**: [Insert brief description, e.g., "Applies business rules. Never performs database writes."]
- **[Insert Module 4 Name]**: [Insert brief description, e.g., "The only layer permitted to interact with the repository."]

## 3. Core Frameworks & Stack
- **JUnit 5**: Default to JUnit Jupiter. Use modern annotations (`@Test`, `@BeforeEach`, `@AfterEach`).
- **Standard Assertions Only**: Strictly use standard JUnit assertions (`org.junit.jupiter.api.Assertions.*`). Do not use AssertJ or Hamcrest.
- **Mockito**: Isolate the source under test using `@ExtendWith(MockitoExtension.class)`. Use `@Mock` for dependencies and `@InjectMocks` for the class under test.
- **Spring Boot Context**: Do NOT use `@SpringBootTest`. Unit tests must be fast, lightweight, and completely isolated from the Spring application context.

## 4. Exhaustive Scenario & Coverage Targeting
Your goal is to generate test suites that achieve **100% line and branch coverage** for the core business logic. Systematically analyze the source code and generate tests for the following:
- **Logical Branching**: Write a distinct test case (or parameterized input) for every `if`, `else if`, `else`, and `switch` case in the method. 
- **Exception Paths**: Explicitly write tests that force the code into every `catch` block. Verify that the correct exception is thrown, caught, and handled in the service layer according to the system's "Fail Fast" principles.
- **Boundary Conditions**: For every numeric or string evaluation, generate test inputs for the exact boundary, just below the boundary, and just above the boundary (e.g., null, empty string, limits).
- **Null Object Pattern**: Always include tests that pass `null` or missing dependencies to verify robust failure handling.
- **Exclusions**: Do NOT write unit tests for pure boilerplate (e.g., standard getters/setters) unless they contain custom business logic. 

## 5. Naming Conventions & Patterns
- **Strict Method Naming**: Test methods MUST follow the exact convention: `methodNameStateUnderTestExpectedBehavior` (camelCase without underscores). 
- **Mandatory Parameterized Testing**: Use `@ParameterizedTest` with `@CsvSource`, `@ValueSource`, or `@MethodSource` whenever multiple inputs map to predictable outputs to avoid test duplication.
- **Arrange-Act-Assert**: Structure all test methods visually into `// Arrange`, `// Act`, and `// Assert` blocks.

## 6. High-Volume Intraday Processing & Async Logic
- **File Handling**: Mock standard Java file watchers (`WatchService`, `WatchKey`, `WatchEvent`) when testing directory polling. Explicitly test that streams, buffers, and file handlers are properly closed (`try-with-resources`).
- **Async Processing (ExecutorService)**: Do not use real multithreading. Mock the `ExecutorService`. Use Mockito's `ArgumentCaptor` to capture the `Runnable` or `Callable` submitted to the executor, and explicitly invoke `.run()` or `.call()` on the captured thread to test the inner asynchronous logic synchronously.

## 7. Domain Context & Test Data Generation
When generating test data, stubbing mocks, or creating inputs for parameterized tests, you MUST use the following supported business products and event types. Do not use generic placeholders (e.g., "test1", "foo"). 

**Supported Products:**
- `[Insert Product 1]`
- `[Insert Product 2]`
- `[Insert Product 3]`

**Supported Events:**
- `[Insert Event 1]`
- `[Insert Event 2]`
- `[Insert Event 3]`

## 8. AI Generation Watermarking
To maintain clear audit trails in the codebase:
- **Class-Level Watermark**: Add a Javadoc comment at the top of the generated test class: `/** AI-Generated by Roo Code */`
- **Method-Level Watermark**: Add a standard comment directly above any generated method signature: `// AI-Generated`

## 9. Code Template Structure

```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * AI-Generated by Roo Code
 */
@ExtendWith(MockitoExtension.class)
class IntradayProcessorTest {

    @Mock
    private ExecutorService executorService;

    @Mock
    private DependencyModule dependencyModule;

    @InjectMocks
    private IntradayProcessor processor;

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    // AI-Generated
    @ParameterizedTest
    @CsvSource({
        "[Insert Product 1], [Insert Event 1], true",
        "[Insert Product 2], [Insert Event 3], true",
        "UNSUPPORTED_PRODUCT, [Insert Event 1], false"
    })
    void processVariousProductEventStatesExpectedResult(String product, String eventType, boolean expectedResult) {
        // Arrange
        // Setup mock behaviors based on input targeting 100% branch coverage
        
        // Act
        boolean actualResult = processor.validateAndQueue(product, eventType);
        
        // Assert
        assertEquals(expectedResult, actualResult, "Validation result did not match expected state.");
    }

    // AI-Generated
    @Test
    void executeAsyncValidDataSubmitsToExecutor() {
        // Arrange
        DomainObject mockData = new DomainObject("[Insert Product 1]", "[Insert Event 1]");
        
        // Act
        processor.processAsync(mockData);
        
        // Assert
        verify(executorService, times(1)).submit(runnableCaptor.capture());
        
        // Extract the runnable and execute it synchronously to test the inner async logic
        RunnaTasRunnaTaskbleCaptor.getValue();
        asyncTask.run();
        
        verify(dependencyModule, times(1)).execute(any());
    }
}



😂



- name: Monitor Actual Scheduled Job Status
  win_shell: |
    # Check the state of the actual Windows task
    $task = Get-ScheduledTask -TaskName "{{ task_name }}"
    if ($task.State -eq 'Running') {
        exit 1  # Still running, tell Ansible to try again
    } else {
        # Task finished! Output the details for the regex check
        schtasks /Query /TN "{{ task_name }}" /V /FO LIST
    }
  register: monitor_result
jdbc:oracle:oci:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcps)(HOST=host)(PORT=port))(CONNECT_DATA=(SERVICE_NAME=service))(SECURITY=(MY_WALLET_DIRECTORY=/path/to/wallet2)))


  # Use 'until' to check the exit code (rc) of the script above
  until: monitor_result.rc == 0
  retries: 1080                # 1080 retries * 60s delay = 18 hours
  delay: 60                    # Check every 60 seconds
  # ASYNC PROTECTION:
  async: 64800                 # Maximum runtime allowed (18 hours)
  poll: 10                     # Re-establish WinRM connection every 10s to verify script status



- name: Extract and Verify Exit Code
  set_fact:
    task_exit_code: "{{ monitor_result.stdout | regex_findall('Last Result:\\s*(\\S+)', '\\1') | first | default('1') | trim }}"

- name: Fail if the task reported a non-zero exit code
  fail:
    msg: "The task finished with error code: {{ task_exit_code }}"
  when: task_exit_code | int != 0






Absolutely — your automation pipeline has evolved into something quite sophisticated.
Here’s a clear, professional summary of the capabilities and smart behavior your pipeline will support — based entirely on our discussions so far.


---

🚀 Automation Test Execution Pipeline — Feature Summary

1️⃣ Multi-VM Distributed Testing

Executes test modules on multiple Windows VMs

Designed for horizontal scale-out


✔ Uses remote execution via Ansible (WinRM)
✔ No dependency on Jenkins agent OS


---

2️⃣ Config-Driven Module Execution

A single YAML file governs:

Module-to-VM mapping

Mutual exclusivity rules

Module discovery



✔ No hardcoding in Jenkinsfile
✔ Test team can adjust config without pipeline changes


---

3️⃣ Parallel Execution Across VMs

Each VM runs tests independently and concurrently

No VM waits for another 🎯


✔ Uses Ansible + Jenkins parallel branches
✔ strategy: free ensures host-level independence


---

4️⃣ Sequential Execution Within Each VM

Maintains controlled execution order per machine


✔ Prevents overloading UI resources
✔ Matches how browser/UI automation must behave


---

5️⃣ Mutually Exclusive Module Protection

Prevents conflicting modules running at the same time


✔ Uses dynamic Jenkins locks
✔ Ensures data integrity across shared business flows
✔ Only the conflicting modules wait (not entire VM branch)


---

6️⃣ Persistent Remote Sessions

Connects once per VM

Runs multiple modules in same logged-in context

Avoids expensive WinRM reconnections


✔ Faster overall execution
✔ Reliable state handling for UI tests


---

7️⃣ Jenkins-Managed Orchestration

Jenkins decides when each module runs

VMs never coordinate with each other


✔ Clean, centralized scheduling intelligence
✔ Full concurrency tracking via Jenkins logs


---

8️⃣ UI Execution Awareness

Designed for UI-driven automation:

✔ Requires logged-in desktop session
✔ Automated validation for session readiness (planned)
✔ Compatible with Selenium + PyAutoGUI


---

9️⃣ Zero Shared Code Impact

Test driver scripts remain untouched

No update needed inside automation framework


✔ Deployment-only improvements, no dev churn


---

🔟 Scalability Built-In

Easily supports:

More VMs → greater parallelism

Additional modules via YAML updates

Multiple test environments (SIT, UAT, PROD-shadow)



---

🧠 Summary in one statement

> A distributed, configuration-driven, parallel UI test execution pipeline
that enforces safety rules through smart scheduling
while maximizing VM utilization and minimizing remote overhead.




---

🚧 Nice-To-Have Enhancements that we can add next

Not implemented yet — optional future features:

Result aggregation + HTML execution summary

Suite-level time & pass/fail tracking

Automatic session readiness validator

Retry policies for flaky modules

Reports archived per VM + uploaded to Jenkins

Notification integration (Mail, Teams, Slack)



---

If you'd like, I can turn this into a design architecture slide or documentation section for your QA DevOps team — with diagrams explaining:

How scheduling works

How exclusivity is enforced

How parallelism is achieved efficiently


Would you like the next output to be:

A️⃣ Architecture diagram (VM orchestration flow)
B️⃣ Deployment documentation for pipeline setup
C️⃣ Reporting/notification upgrade plan
D️⃣ All of the above

Pick A, B, C, or D.










def loadDependencies = {
                        def config = readYaml file: 'suite-dependencies.yml'
                        return config.suites
                    }

                    def parseUserSelection = {
                        return params.SELECTED_SUITES.split(/\s+/) as List
                    }

                    def computeExecutionOrder = { deps, selected ->
                        def order = []
                        selected.each { suite ->
                            if (!deps.containsKey(suite)) {
                                error "Unknown suite '${suite}' in config"
                            }
                            order.addAll(deps[suite])
                            order.add(suite)
                        }
                        return order.unique() // final dedupe preserving order
                    }

                    def validateExecutionOrder = { deps, order ->
                        order.eachWithIndex { suite, idx ->
                            def req = deps[suite] ?: []
                            req.each { dep ->
                                if (order.indexOf(dep) > idx) {
                                    error """
                                    Dependency violation detected:
                                      '${suite}' appears before '${dep}'
                                    Correct this in suite-dependencies.yml
                                    """
                                }
                            }
                        }
                        echo "Dependency validation passed ✔"
                    }



''
- name: Run UI Automation Tests in Interactive Mode
  hosts: windows
  gather_facts: no

  vars:
    task_name: "Run_UI_Tests"
    python_interpreter: "python.exe" # or full path if needed

  tasks:
    # Step-1: Make sure there is an active interactive console session
    - name: Detect Active Console Session ID
      win_shell: |
        $session = quser | Select-String "Active"
        if ($session) {
          ($session -split '\s+')[2]
        } else {
          ""
        }
      register: console_session

    - name: Attach to console session if not active
      win_shell: |
        $sid = quser | Select-String "{{ ansible_user }}" | % { ($_ -split '\s+')[2] }
        if ($sid) {
          tscon $sid /dest:console
        }
      when: console_session.stdout == ""

    # Step-2: Generate delayed start boundary (90 seconds ahead)
    - name: Calculate scheduled start time
      set_fact:
        start_time: "{{ '%Y-%m-%dT%H:%M:%S' | strftime(ansible_date_time.epoch | int + 120) }}"

    # Step-3: Create Scheduled Task with interactive UI
    - name: Create scheduled task to run UI python tests
      community.windows.win_scheduled_task:
        name: "{{ task_name }}"
        description: "Run UI tests in real desktop session"
        enabled: yes
        run_level: highest
        logon_type: password
        username: "{{ ansible_user }}"
        password: "{{ ansible_password }}"
        triggers:
          - type: time
            start_boundary: "{{ start_time }}"
        actions:
          - path: "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe"
            arguments: >
              -WindowStyle Maximized -Command "& {
                $env:service_account_username='{{ service_account_username }}';
                $env:service_account_password='{{ service_account_password }}';
                $env:target_environment='{{ target_environment }}';
                $env:project='{{ project }}';
                {{ python_interpreter }} '{{ python_script }}' '{{ test_report_path }}' |
                Out-File 'C:\Temp\tcoe-log.txt' -Append
              }"
        state: present
      register: task_created

    # Step-4: Run task immediately if needed
    - name: Start scheduled task immediately
      win_scheduled_task:
        name: "{{ task_name }}"
        state: running
      when: task_created is changed

    - debug:
        msg: "Task '{{ task_name }}' created and triggered successfully!"

''
[project]
name = "uat-automation"
version = "1.0.0"
description = "Robot Framework based UAT automation with Selenium"
requires-python = ">=3.10"

dependencies = [
    "robotframework>=6.1",
    "robotframework-seleniumlibrary>=6.0",
    "selenium>=4.20",
    "webdriver-manager>=4.0"
]

[tool.uv]
# optional: keeps .venv inside project root
venv = ".venv"


robotframework>=6.1
robotframework-seleniumlibrary>=6.0
selenium>=4.20
webdriver-manager>=4.0


# TechSpec AI Agent


-Djdk.internal.httpclient.disableHostnameVerification

This project analyzes Java Spring Boot code, SQL DDLs, and generates technical documentation in Markdown format.

# System Architecture Summary

## 1. Overview

This system processes financial deal files through a multi-stage asynchronous pipeline using Spring Boot. The pipeline includes parsing, processing, and XML generation phases. Each stage operates with its own executor and can handle tasks concurrently while maintaining deal-level sequential consistency.

## 2. Component/Class Overview

| Component                       | Responsibility                                                               |
| ------------------------------- | ---------------------------------------------------------------------------- |
| `FileWatcherService`            | Monitors directories for new files and initiates processing.                 |
| `FileProcessorService`          | Parses raw deal files and extracts identifiers.                              |
| `DealProcessorService`          | Transforms parsed deal data using business rules.                            |
| `XmlGeneratorService`           | Converts transformed data into the final XML format.                         |
| `ConsistentHashingTaskExecutor` | Ensures all tasks for a single deal go to the same single-threaded executor. |
| `DealFileQueueManager`          | Queues and serializes processing of files per deal.                          |
| `LoggingConfig`                 | Sets up executor-based and thread-based loggers.                             |

## 3. Threading & Concurrency Design

* Each major component uses a dedicated `ThreadPoolTaskExecutor`.
* The `ConsistentHashingTaskExecutor` hashes `dealId` to one of N single-threaded executors.
* MDC and `TaskDecorator` are used to propagate logging context.
* Executors use `CallerRunsPolicy` to avoid silent task drops.

## 4. Directory & File Flow

| Deal Status  | Source Dir       | Destination Dir  |
| ------------ | ---------------- | ---------------- |
| `RECEIVED`   | `/input`         | `/tmp`           |
| `DEAL_READY` | `/tmp`           | `/fileprocessed` |
| `PROCESSED`  | `/fileprocessed` | `/xml`           |
| `ERROR`      | Any              | `/error`         |

### Special Case: `DEAL_READY`

* From `FileProcessorService`: Move from `/tmp` → `/fileprocessed`.
* From `DealProcessorService`: Move from `/fileprocessed` → `/tmp`.

## 5. Status Routing Logic (Enum-Based)

```java
public enum DealStatus {
    DEAL_READY {
        public Path getSource(ProcessingStage stage) {
            return stage == FILE_PROCESSING ? TMP : FILE_PROCESSED;
        }
        public Path getDestination(ProcessingStage stage) {
            return stage == FILE_PROCESSING ? FILE_PROCESSED : TMP;
        }
    }
    // ... other statuses
}
```

## 6. Logging Strategy

* Executors are prefixed (e.g., `dealExecutor-1`, `fileExecutor-1`).
* MDC stores `threadName` as `dealThread`.
* Sifting Appender writes logs per thread to `logs/dealExecutor-1.log`, etc.
* `ConsumerExecutor` logs are routed via MDC propagation.

## 7. Performance Targets

* **Files:** 10,000
* **Duration:** 8 hours
* **Target:** \~300 files/minute
* **Threads per stage:** Max 5 (configurable)
* **Deal-level concurrency:** Sequential per deal

## 8. Key Constraints

* Spring Boot batch job (non-web)
* Spring JDBC with HikariCP
* Spring Boot starter for Jersey REST (for optional APIs)
* Conditional secondary DB (enabled by XML property at startup)
* Not using Spring Batch

## 9. Error Handling & Recovery

* Exceptions during processing are caught and status is set to `ERROR`
* Files moved to `/error` dir
* Overflow events from `WatchService` trigger fallback sync
* Fallback sync avoids re-queuing already queued files

## 10. Java Watcher Overflow Handling

* Queue overflow is expected under high volume
* Uses fallback sync thread to scan and enqueue files manually
* Files already in executor queues are skipped

## 11. Logging Pipeline Flow

```java
DealExecutor → submits → ConsumerExecutor
           ↳ MDC Decorator adds `dealThread`
ConsumerExecutor ↳ inherits MDC context
Logs from both go to `logs/dealExecutor-<X>.log`
```

## 12. Suggestions for Future Enhancements

* Replace DB polling with Event-Driven messaging
* Introduce in-memory state coordination (e.g., Redis/MapDB)
* Explore batching file tasks by deal instead of individual
* Log-level telemetry using Micrometer + Grafana dashboards

## 13. Monitoring & Observability

* Thread counts, queue sizes, rejection metrics (via custom meters)
* Log volume per deal executor
* Async appender health
* Disk I/O during fallback sync

## 14. Other Notes

* Uses conditional bean creation and `@Primary` for DB routing
* Uses fallback sync with `BlockingQueue` where Watcher fails
* Extensible `Enum` status routing strategy
* 

Here's a structured prompt you can use to prepare a presentation for a meeting:


---

Prompt Template
"I need help creating a presentation for an upcoming meeting. Here are the details:

Meeting Objective: [e.g., present Q2 results, propose a new project, align on strategy]

Audience: [e.g., senior leadership, project team, clients]

Time Limit: [e.g., 10 minutes, 30 minutes]

Key Points to Cover:

1. [e.g., performance metrics]


2. [e.g., challenges and solutions]


3. [e.g., next steps]



Tone and Style: [e.g., formal, persuasive, informative, concise]

Visual Aids Needed: [e.g., charts, graphs, screenshots, bullet points]

Any Specific Tools/Format Required: [e.g., PowerPoint, Google Slides]


Please help me outline the presentation and suggest slide titles with key content for each."


---

Would you like me to fill in this prompt with your current meeting details?



Here are 2–3 high-level points suitable for functional managers:


---

🔹 Key Highlights for Functional Managers

1. Fast & Reliable Deal Processing

Processes thousands of financial deal files per hour with guaranteed accuracy and traceability.



2. Modular & Extensible Design

Easily supports new deal types, validation rules, and transformation logic without impacting existing flows.



3. Automated Error Handling & Recovery

Built-in safeguards ensure no deal is lost; issues are isolated and logged for review.





---

Let me know if you'd like this phrased for email, slide, or status report.





