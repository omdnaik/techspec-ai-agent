// Wave 1 Sync Action: Initialize AGENTS.md in the repo workspace natively first
console.log("⚙️ Initialising workspace blueprints...");
const initProcess = spawn.sync('opencode.cmd', ['init'], { shell: true });

Better error logging just in case
if (initProcess.error || initProcess.status !== 0) {
    const errorMsg = initProcess.error ? initProcess.error.message : `Exit code ${initProcess.status}`;
    writeLog(`❌ Failed to run 'opencode init': ${errorMsg}`);
    process.exit(1);
}

if (initProcess.error) {
  console.error("❌ Failed to run 'opencode init':", initProcess.error);
  process.exit(1);
}

console.log("🚀 AGENTS.md initialized successfully. Triggering Analysis Agents...");


Wave 1 Sync Action: Initialize AGENTS.md in the repo workspace natively first
writeLog("⚙️ Initialising workspace blueprints...");

try {
    // Await a custom Promise that wraps your spawn call
    await new Promise((resolve, reject) => {
        const initProcess = spawn('opencode.cmd', ['run', '--command', 'init', '--dir', repoRoot, '--print-log'], {
            windowsHide: true,
            stdio: ['inherit', 'pipe', 'pipe'],
            shell: true, // <--- This fixes the EINVAL crash & auto-resolves the extension
            env: {
                ...process.env,
                OPENCODE_DISABLE_MODELS_FETCH: 'true',
                OPENCODE_DISABLE_DEFAULT_PLUGINS: 'true'
            }
        });

        // Listen for the process to finish
        initProcess.on('close', (code) => {
            if (code === 0) {
                resolve(); // Success! Move on.
            } else {
                reject(new Error(`Process exited with code ${code}`)); // Failed!
            }
        });

        // Listen for process-level errors (like failing to start at all)
        initProcess.on('error', (err) => {
            reject(err);
        });
    });
} catch (error) {
    // This catches the rejections from the Promise above
    writeLog(`❌ Failed to run 'opencode init':`, error);
    process.exit(1);
}

        // 3c. The optimized, deterministic prompt
        const prompt = `You are a Principal Systems Architect. Your audience is HUMAN software engineers. You must do the deep analysis yourself. DO NOT delegate.

Step 1: Use your Jira MCP tools to fetch ticket ${jiraKey}. Extract the standard description and 'customfield_14724' (OMR Description) to fully understand the goal.
Step 2: Do NOT blindly harvest or guess files. Use the 'depwire_get_architecture_summary' and 'depwire_get_file_context' MCP tools to map the entry points, business logic, and data persistence models deterministically. If you propose modifying a symbol, use 'depwire_impact_analysis' to find its exact blast radius and downstream consumers.
Step 3: Database Script Analysis. Use the native 'ls' tool recursively to locate the 'hubs' directory and find.sql scripts inside 'hubs/*/sql'. Use the 'read' tool to inspect them.
Step 4: Synthesize these deterministic facts into a highly detailed, human-readable Markdown blueprint detailing the exact source files to be modified and the structural impact. Output the complete blueprint directly as your final text response in the console.`;

