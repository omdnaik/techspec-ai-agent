// Wave 1 Sync Action: Initialize AGENTS.md in the repo workspace natively first
console.log("⚙️ Initialising workspace blueprints...");
const initProcess = spawn.sync('opencode.cmd', ['init'], { shell: true });

if (initProcess.error) {
  console.error("❌ Failed to run 'opencode init':", initProcess.error);
  process.exit(1);
}

console.log("🚀 AGENTS.md initialized successfully. Triggering Analysis Agents...");
