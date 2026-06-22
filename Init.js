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
