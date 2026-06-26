This is an incredibly sharp architectural addition. Hardcoding the pipeline to expect Spring Boot and JPA creates a brittle system. If a team spins up a lightweight Micronaut or Quarkus microservice, a vanilla Java batch job, or even a Node.js/Go companion service within the same ecosystem, the downstream agents will fail or hallucinate because they are searching for annotations like @Autowired and @Qualifier that simply do not exist.
To make your impact analysis system truly polyglot and adaptive, you should introduce a **Tech Detector Agent** right at the beginning of Phase 2 (Phase 1.5). This agent dynamically inspects the local directory, determines the ecosystem, and writes a technology contract that all subsequent agents must read to adapt their parsing strategies.
Here is the updated architecture and the complete configurations to inject tech-stack adaptation into your stack.
### The New Architecture Payload (0_tech_stack.json)
**File Name:** .opencode/0_tech_stack.json
This contract is written by the new detector agent. Downstream agents will parse this to know *how* to search for dependencies and *where* configurations live.
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["language", "framework", "build_system", "di_mechanism", "config_patterns"],
  "properties": {
    "language": { "type": "string", "enum": ["JAVA", "KOTLIN", "JAVASCRIPT", "TYPESCRIPT", "GO", "PYTHON"] },
    "framework": { "type": "string", "enum": ["SPRING_BOOT", "QUARKUS", "MICRONAUT", "VANILLA", "EXPRESS", "NESTJS"] },
    "build_system": { "type": "string", "enum": ["MAVEN", "GRADLE", "NPM", "GO_MOD", "PIP"] },
    "di_mechanism": { 
      "type": "string", 
      "enum": ["SPRING_ANNOTATIONS", "JAKARTA_CDI", "MANUAL_WIRING", "NEST_INJECTION", "NONE"] 
    },
    "config_patterns": {
      "type": "array",
      "items": { "type": "string" },
      "description": "File extensions and naming patterns to search for config (e.g., ['*.yml', '*.properties', '*.env', 'package.json'])"
    }
  }
}

```
### 1. The New Agent: Tech Detector (tech-detector.md)
Place this prompt in .opencode/agents/tech-detector.md. It runs right after the jira-agent and uses local terminal utilities to look for "fingerprints" of the tech stack.
```markdown
# Role: Local Environment & Tech Stack Detector
You are a precise system infrastructure scout. Your sole responsibility is to analyze the local repository files to identify the programming language, framework, build system, and configuration management engine used by the micro-application.

## STRICT DETECTION ROUTINE
1. Use your local terminal tools to inspect the workspace root directory (e.g., executing list or checking file presence via standard shell tests).
2. **Identify the Build System:**
   - Presence of `pom.xml` = MAVEN (Language: JAVA/KOTLIN)
   - Presence of `build.gradle` or `build.gradle.kts` = GRADLE (Language: JAVA/KOTLIN)
   - Presence of `package.json` = NPM (Language: JAVASCRIPT/TYPESCRIPT)
   - Presence of `go.mod` = GO_MOD (Language: GO)
3. **Identify Framework & Dependency Injection (DI) Mechanisms:**
   - If Java/Kotlin, check dependencies inside build files:
     - Contains `spring-boot` = Framework: SPRING_BOOT, DI: SPRING_ANNOTATIONS
     - Contains `quarkus` = Framework: QUARKUS, DI: JAKARTA_CDI (e.g., `@Inject`, `@Named`)
     - Contains `micronaut` = Framework: MICRONAUT, DI: JAKARTA_CDI
     - If none, Framework: VANILLA, DI: MANUAL_WIRING
   - If Node.js, check `package.json`:
     - Contains `@nestjs` = Framework: NESTJS, DI: NEST_INJECTION
     - Contains `express` = Framework: EXPRESS, DI: NONE
4. **Determine Configuration File Patterns:**
   - For Spring/Quarkus/Micronaut, set config patterns to `["*.yml", "*.yaml", "*.properties"]`.
   - For Node.js/Nest, set config patterns to `["*.env", "package.json", "*.json"]`.
5. Write the final deterministic values to `.opencode/0_tech_stack.json` matching the provided schema exactly. Terminate immediately upon writing.

```
### 2. Upgrading Downstream Agents to be Polyglot
Now, we rewrite the **Guided Explorer** and **Config Scout** prompts so they don't hardcode Spring logic. Instead, they dynamically adjust based on what the tech-detector found.
#### Updated Section for Guided Explorer (guided-explorer.md)
Replace the Phase 2 routine in your current prompt with this adaptive logic:
```markdown
### Phase 2: Deep Read & Polyglot Runtime Resolution
1. Read `.opencode/0_tech_stack.json` to extract the `framework` and `di_mechanism`.
2. Open and read the exact files identified via the Depwire index (applying the 150-line God Class restriction using `read_method_body`).
3. **ADAPTIVE DEPENDENCY INJECTION RESOLUTION:**
   - **IF SPRING_ANNOTATIONS:** Look for `@Autowired` and `@Qualifier("beanName")`. Pivot to Depwire to query for implementations of the interface, then match the concrete class annotated with `@Service("beanName")` or `@Component("beanName")`.
   - **IF JAKARTA_CDI (Quarkus/Micronaut):** Look for `@Inject` and `@Named("beanName")`. Pivot to Depwire to query for implementations, then find the concrete class annotated with `@ApplicationScoped`, `@Singleton`, and matching `@Named`.
   - **IF NEST_INJECTION (NestJS):** Look for constructor injection and `@Injectable()`. Review the local module definitions (`*.module.ts`) to locate the mapped providers array.
   - **IF MANUAL_WIRING / NONE:** Trace explicit instantiation (e.g., `new ConcreteService()`) or structural Factory methods inside the file execution block.

```
#### Updated Section for Config & SQL Scout (config-scout.json)
Modify its system prompt to dynamically target files based on the tech stack:
```text
STRICT INSTRUCTIONS:
1. Read `.opencode/0_tech_stack.json` to extract the `config_patterns` array.
2. Read `.opencode/1_business_scope.json` for the `search_keywords`.
3. Use the local terminal tool to execute search queries (e.g., grep) targeting ONLY the file types explicitly specified in the `config_patterns` array, along with raw `.sql` schema migration files. 
4. Do not limit searches to src/main/resources/ if the project configuration patterns indicate non-Java ecosystems (e.g., scanning project roots for .env files in Node.js apps).
5. Output findings strictly to .opencode/3b_config_impacts.json.

```
### 3. Updating the Pipeline Coordinator (coordinator.md)
You must update your master orchestrator to boot this step first:
```markdown
## Execution Sequence (STRICT ROUTINE)
1. **Step 0: Tech Stack Environment Scan**
   - Execute `tech-detector`.
   - Wait until `.opencode/0_tech_stack.json` is generated and verified.

2. **Step 1: Domain Definition**
   - Execute `jira-agent`.
   - Wait for `.opencode/1_business_scope.json`.
... [rest of the pipeline remains sequential]

```
### Why this Completes the Enterprise Stack
By executing this automated "fingerprint scan" at the start, your agent pipeline functions like an adaptive compiler plugin. It assesses the codebase's specific architectural philosophy before attempting any parsing or graph queries, ensuring that the final output maintains extreme accuracy and implementation depth whether analyzing a heavy enterprise Spring Core microservice or a lightweight cloud-native Go/Quarkus lambda function.
