# Hackathon Challenge: Project "BridgeDock"
**Theme:** Enterprise Platform Engineering, Test Observability, and Developer Experience (DevEx)
**Duration:** 24 Hours
**Team Size:** 6 Developers
### 📖 The Backstory (Why You Are Building This)
Imagine this: You have just spent hours writing a flawless new feature in Spring Boot. You are ready to push your code, but first, you need to run your integration tests to guarantee your application correctly talks to the database and external services.
There is just one massive problem. Your laptop doesn't have Docker installed due to strict corporate security policies, or perhaps it simply lacks the RAM to spin up four different database containers simultaneously. Your local tests fail. You are completely blocked.
In the real world of distributed enterprise systems, local infrastructure is a massive bottleneck. Developers waste countless hours debugging "it works on my machine" errors, trying to configure SSH tunnels manually, or copying and pasting thousands of lines of messy terminal logs into chat apps to ask senior engineers for help.
### 🎯 The Core Challenge
Your mission is to build **BridgeDock**, a zero-configuration Developer Experience (DevEx) tool and centralized observability platform.
You must build a custom Java testing framework that seamlessly offloads the heavy lifting of integration testing (via Testcontainers) to a remote cloud server. The local developer should not have to understand networking. Your framework must automatically build a secure SSH bridge, execute the infrastructure remotely, and trick the local application into communicating over that bridge.
Alongside the backend framework, your team will build a full-stack web dashboard that tracks these remote testing environments, visualizes the test pipelines in real time, and provides a collaborative, forensic debugging UI for when tests inevitably crash.
### 🚀 The "Zero-Config" Developer Onboarding
For an enterprise tool to be successful, developers must actually want to use it. Your framework must guarantee a frictionless, "two-minute onboarding" experience for any legacy Spring Boot application. The end-user (a developer testing their code) should only have to complete these three steps:
 1. **Add the Dependency:** The developer drops your custom bridgedock-spring-test-starter JAR into their pom.xml or build.gradle.
 2. **Declare the Target:** The developer adds a single line to their application-test.yml specifying the remote cloud target (e.g., cluster-target: "dev-pool-1").
 3. **Run the Tests:** The developer clicks "Play" in their IDE or types mvn test.
**The Magic:** Without the developer changing a single line of their Java test code, your framework must invisibly wake up, intercept the startup context, build the secure SSH tunnel, dynamically map the local ports, and route Testcontainers to the remote cloud server.
### 📈 The Scope & Tiers of Success
This project is designed to scale with your team's capability. Focus on nailing the core engine before moving to the advanced DevEx features.
**Tier 1: The Core Bridge (Minimum Viable Product)**
 * Build a custom JUnit 5 or Spring Test listener that dynamically establishes a secure connection to the provided remote Docker daemon.
 * Automate the local port-forwarding so the local application context can successfully query the remote databases.
 * Build a frontend dashboard that tracks which developer is using the remote server and displays a final summary of their test run (Pass/Fail).
**Tier 2: The Real-Time Command Center (Targeted Goal)**
 * Implement a silent autoconfiguration strategy (via Spring EnvironmentPostProcessor) so the onboarding developer doesn't need to add any custom annotations to their code.
 * Upgrade the frontend to receive real-time test execution events via WebSockets, rendering a live view of the test suite exactly as it executes locally.
 * Create a chronological log-weaver in the UI that displays local application logs interleaved directly with the remote database logs to instantly spot anomalies.
**Tier 3: The Enterprise Platform (Elite Level)**
 * Build a Java-based TCP proxy to handle the secure tunneling natively, completely bypassing the need for local OS shell scripts or basic terminal commands.
 * Implement a forensic "Snapshot" feature that captures the exact environment state, Spring profiles, and connection pool status the millisecond a test fails.
 * Generate unique, shareable permalinks for failed test runs so developers can instantly collaborate on debugging complex system crashes without pasting text files.
### 🛠️ The Approved Tech Stack
To ensure a level playing field and test true architectural engineering, you must adhere to the following stack constraints. **Bypassing the Java layer using native OS shell scripts (Runtime.getRuntime().exec()) to trigger SSH or Docker is strictly forbidden.**
 * **The Core Engine (Java):** Java 21+, Spring Boot 3.x, JUnit 5 (TestExecutionListener), and Java Testcontainers.
 * **The Network & Security Bridge:** apache-mina-sshd (Mandatory for tunneling) and docker-java (Official API client).
 * **The Web Dashboard:** React/Next.js, Tailwind CSS, and stompjs / SockJS for WebSocket real-time data streaming.
 * **The Target Host:** You will be provided with an SSH key and an IP address to a remote Linux server running a bare-metal Docker daemon.
### 🏆 Evaluation & Judging Criteria

| Criteria | Description |
| :--- | :--- |
| **Architectural Integrity** | Did you successfully orchestrate the remote network bridge entirely within the JVM, honoring the constraints? |
| **Frictionless Onboarding** | How seamless is it for a new developer to adopt your tool? Does it truly honor the "Zero-Config" promise? |
| **Observability Value** | Does the frontend dashboard actually make debugging easier? Are the logs correlated logically and visually intuitive? |
| **Resiliency & Cleanup** | If a developer aggressively kills their IDE mid-test, does your framework gracefully clean up the remote cloud container, or does it leave a zombie process burning resources? |

### ❓ Frequently Asked Questions (FAQ)
#### Network & Infrastructure Concerns
 * **Our college Wi-Fi / hostel network blocks outbound SSH port 22. How do we connect to the remote host?**
   If your local network restricts outbound traffic on port 22, please notify the organizing team immediately. We can reconfigure the remote Linux daemon to listen on an open alternative port (like 443 or 8080). Alternatively, you can use a mobile data hotspot during the testing phase, as standard cellular networks do not block port 22 traffic.
 * **What happens if our mobile network drops or lags heavily mid-test? Will the whole application crash?**
   Network latency is a core part of this engineering challenge. Over a lagging connection, standard timeouts will trigger. To prevent crashes, your framework needs defensive error handling: you must explicitly configure higher connection timeouts in Testcontainers and optimize your Apache MINA socket parameters.
 * **If multiple teams are running tests simultaneously against the same remote server, won't our database container ports collide?**
   Yes, if you hardcode local-to-remote port mappings (like mapping port 5432 to 5432), they will collide instantly. A key requirement of the "Core Engine" is to let the remote Docker daemon assign a random ephemeral port on the host, read that dynamic port via the Docker Java API, and then bind it to a dynamic free port on your local laptop.
#### Environment & Compatibility Questions
 * **Most of our team members use Windows laptops without WSL. Will this framework still work?**
   Yes. Because you are mandated to use pure Java libraries (apache-mina-sshd) instead of executing native OS bash scripts, your code remains completely platform-independent. It will run identically on Windows CMD, Git Bash, macOS, or Linux without needing WSL.
 * **Are we allowed to use Spring Boot 2.x instead of 3.x if we are more comfortable with it?**
   No. Enterprise standards require modern dependencies. Spring Boot 3.x enforces Java 17/21 baselines and provides native support for @DynamicPropertySource, which is vital for injecting dynamic network ports into your test context on the fly.
 * **Can we use automated libraries like Lombok, or should we write everything explicitly?**
   You can use Lombok for simple boilerplate (like getters, setters, and constructors) to save time. However, any core framework logic—such as abstract proxy classes, custom JUnit extensions, or your service connection handlers—must be explicitly written by your team so the judges can evaluate your architectural choices.
#### Feature Scope & Judging Rules
 * **What exactly constitutes "cheating" when setting up the SSH connection?**
   Using Runtime.getRuntime().exec() or ProcessBuilder to execute ssh -L ... via your laptop's terminal is strictly forbidden. The entire SSH tunnel handshake, channel multiplexing, and port forwarding must be managed inside the application memory using the apache-mina-sshd library API.
 * **Does the frontend dashboard need a persistent database to save test history?**
   For Tier 1 and Tier 2, storing data in a temporary, in-memory structure (like a concurrent Java map) or an ephemeral cache within your Spring Boot backend server is perfectly acceptable. For Tier 3 elite status, persisting this run data into an active database to track historical test failure rates over time will earn significant bonus points.
 * **If a developer aggressively kills their local JUnit test using the "Stop" button in IntelliJ, how do we clean up the remote containers?**
   This is the ultimate resiliency check. When an IDE kills a JVM process, standard teardown hooks might not fire. A robust architecture will utilize an automated background reaper process or a short live-ping heartbeat mechanism between the Core Engine and the remote host to ensure orphaned containers are automatically swept.
