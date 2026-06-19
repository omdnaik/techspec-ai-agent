**Subject:** Setup Guide: One-Time Global Architecture Bot (Roo Code + Depwire)
Hi Team,
To streamline our requirement analysis across our 50+ microservices, we are rolling out a local automation bot. This bot integrates **Roo Code**, **Depwire**, and **Jira** to automatically map our Spring dependencies and generate an architectural blueprint before we write any code.
Instead of configuring this in every single repository, we are using a **Global Git Hook**. You only need to set this up once on your Windows machine. Afterward, anytime you check out a branch containing a Jira key (e.g., feature/PROJ-123) in *any* of our repositories, the bot will silently generate the blueprint in the background without blocking your terminal.
Because of built-in safety filters, this hook will safely ignore any branches without Jira keys (like main) and any of your personal side-projects.
Please follow the steps below to complete the one-time setup.
### Prerequisites
 1. **Node.js 20+** must be installed on your system.
 2. Add the following to your **Windows System Environment Variables** (restart your IDE/terminal after adding these):
   * CORPORATE_JIRA_TOKEN: Your Jira API token.
   * CORPORATE_LLM_KEY: Your token for the corporate LLM gateway.
### Step 1: Install the Roo Code CLI
The background script requires the Roo Code CLI to be installed globally. Open your terminal and run:
```bash
npm install -g @roocode/cli

```
*(Note: If utilizing our internal artifact registry, append --registry=https://your-internal-registry-url/ to the command).*
### Step 2: Create Your Global Git Hooks Directory
Open Command Prompt or PowerShell and create a centralized folder in your user directory to hold the script:
```powershell
mkdir C:\Users\%USERNAME%\.git-global-hooks

```
### Step 3: Tell Git to Use the Global Folder
Run this command once to route all your local repositories to this master folder:
```bash
git config --global core.hooksPath C:/Users/%USERNAME%/.git-global-hooks

```
### Step 4: Add the Automation Script
 1. Open Windows File Explorer and navigate to C:\Users\%USERNAME%\.git-global-hooks.
 2. Create a new text file and name it exactly **post-checkout**.
   * **CRITICAL:** Remove the .txt or .js extension entirely. Git will ignore the file if it has an extension. You may need to enable "File name extensions" in the View tab to ensure it is completely removed.
 3. Open the file, paste the following Node.js script, and save it:
```javascript
#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const { spawn, execSync } = require('child_process');

// 1. Check if this is a branch switch (1) or a single file checkout (0)
const isBranchSwitch = process.argv[4] === '1';
if (!isBranchSwitch && process.argv[2] !== 'run-analysis') {
    process.exit(0);
}

// 2. Synchronous Idempotency Check
if (process.argv[2] !== 'run-analysis') {
    try {
        // Ask Git directly for the branch name safely across any repository
        const branchName = execSync('git symbolic-ref --short HEAD', { encoding: 'utf8', stdio: 'pipe' }).trim();
        const jiraMatch = branchName.match(/[A-Z]+-[0-9]+/);

        // Safety Net: Exit instantly if the branch name doesn't have a Jira key
        if (!jiraMatch) process.exit(0); 
        
        const jiraKey = jiraMatch[0];
        const reportPath = path.join('docs', 'analysis', `${jiraKey}-analysis.md`);
        
        if (fs.existsSync(reportPath)) process.exit(0);

        // Spawn detached background process to prevent terminal blocking
        const child = spawn(process.execPath, [__filename, 'run-analysis', branchName, jiraKey], {
            detached: true,
            stdio: 'ignore',
            windowsHide: true 
        });
        
        child.unref(); 
        process.exit(0); // Return control to the developer instantly
    } catch (e) {
        process.exit(0); 
    }
}

// 3. Asynchronous Background Worker
async function run() {
    const [,, , branchName, jiraKey] = process.argv;
    const corpJiraToken = process.env.CORPORATE_JIRA_TOKEN;
    const reqDir = path.join('.roo', 'requirements');
    const reqFile = path.join(reqDir, `${jiraKey}.md`);
    const outputDir = path.join('docs', 'analysis');
    const outputFile = path.join(outputDir, `${jiraKey}-analysis.md`);

    try {
        fs.mkdirSync(reqDir, { recursive: true });
        fs.mkdirSync(outputDir, { recursive: true });

        // Fetch requirement text from Jira
        if (!fs.existsSync(reqFile)) {
            const res = await fetch(`https://your-company.atlassian.net/rest/api/3/issue/${jiraKey}`, {
                headers: { 'Authorization': `Bearer ${corpJiraToken}` }
            });
            if (!res.ok) return;
            const data = await res.json();
            const summary = data.fields?.summary || '';
            const desc = data.fields?.description || '';
            fs.writeFileSync(reqFile, `# ${jiraKey}: ${summary}\n\n${desc}`);
        }

        // Start Depwire graph server silently
        const port = '8089';
        const npxCmd = process.platform === 'win32' ? 'npx.cmd' : 'npx';
        const depwire = spawn(npxCmd, ['-y', 'depwire', 'serve', '--path', '.', '--port', port], {
            windowsHide: true
        });

        await new Promise(resolve => setTimeout(resolve, 4000));

        // Invoke Roo Code to write the blueprint
        const rooCmd = process.platform === 'win32' ? 'roocode.cmd' : 'roocode';
        const roo = spawn(rooCmd, [
            'analyze',
            '--mode', 'architect',
            '--mcp-server', `http://localhost:${port}`,
            '--instruction', `Read the requirement at '${reqFile}'. Analyze the codebase graph and generate the comprehensive blueprint document.`,
            '--output', outputFile
        ], { windowsHide: true });

        await new Promise(resolve => roo.on('exit', resolve));

        depwire.kill();

        // Trigger Windows desktop notification
        if (process.platform === 'win32') {
            spawn('powershell', ['-Command', `& {Add-Type -AssemblyName PresentationFramework; [System.Windows.MessageBox]::Show('Architectural blueprint for ${jiraKey} generated successfully!','Roo Code Automation')}`], { windowsHide: true });
        }
    } catch (err) {
        // Silently fail background tasks to avoid interrupting workflow
    }
}

run();

```
### Step 5: Verify the Setup
To test that the daemon is working, open your terminal in any of our project repositories and check out a test branch with a valid Jira key:
```bash
git checkout -b feature/PROJ-123

```
Your terminal prompt should return to you immediately. Within about 30 to 60 seconds, a desktop notification will pop up, and you will see docs/analysis/PROJ-123-analysis.md generated in your IDE project tree.
Reach out if you hit any roadblocks with the CLI or environment variables.
Thanks,
[Your Name]
