**Subject:** Setup Guide: Automated Architecture Analysis Bot (Roo Code + Depwire)
Hi Team,
To streamline our requirement analysis and ensure we have a solid architectural plan before we start writing code, we are rolling out a local automation bot.
This bot integrates **Roo Code**, **Depwire**, and **Jira**. Once configured, every time you check out a new feature branch containing a Jira key (e.g., feature/PROJ-123), a background Git hook will automatically download the Jira ticket, map our codebase dependencies, and generate a comprehensive markdown blueprint in your project tree—all without freezing your terminal or IDE.
Since we want to keep our repository clean of local environment configurations, this setup will be done locally in your untracked .git folder.
Please follow the steps below to configure this on your Windows machine.
### Prerequisites
 1. **Node.js 20+** must be installed on your system.
 2. Add the following to your **Windows System Environment Variables** (a restart of your IDE/terminal is required after adding these):
   * CORPORATE_JIRA_TOKEN: Your Jira API token.
   * CORPORATE_LLM_KEY: Your token for the corporate LLM gateway.
### Step 1: Install the Roo Code CLI
You need the CLI installed globally on your machine so the background script can invoke it. Open your terminal and run:
```bash
npm install -g @roocode/cli

```
*(Note: If we are routing this through an internal artifact registry, append --registry=https://your-internal-registry-url/ to the command).*
### Step 2: Create the Local Git Hook
We are using a Git post-checkout hook to trigger the analysis asynchronously.
 1. Clone the repository if you haven't already.
 2. Open Windows File Explorer (ensure **"Show Hidden Files"** is enabled in the View tab).
 3. Navigate to the hidden .git/hooks/ directory at the root of the project.
 4. Create a new text file and name it exactly **post-checkout**.
   * **CRITICAL:** Remove the .txt or .js extension entirely. Git will ignore the file if it has an extension.
### Step 3: Add the Automation Script
Open the post-checkout file in your preferred text editor, paste the following Node.js script, and save it. Remember to update the Atlassian URL in the fetch command to match our corporate domain.
```javascript
#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const { spawn } = require('child_process');

// 1. Check if this is a branch switch (1) or a single file checkout (0)
const isBranchSwitch = process.argv[4] === '1';
if (!isBranchSwitch && process.argv[2] !== 'run-analysis') {
    process.exit(0);
}

// 2. Synchronous Idempotency Check (Runs attached to Git)
if (process.argv[2] !== 'run-analysis') {
    try {
        const headFile = fs.readFileSync(path.join('.git', 'HEAD'), 'utf8').trim();
        const branchName = headFile.replace('ref: refs/heads/', '');
        const jiraMatch = branchName.match(/[A-Z]+-[0-9]+/);

        if (!jiraMatch) process.exit(0); // Exit if no Jira key in branch name
        const jiraKey = jiraMatch[0];

        // Hard Gate: Skip if the report already exists
        const reportPath = path.join('docs', 'analysis', `${jiraKey}-analysis.md`);
        if (fs.existsSync(reportPath)) process.exit(0);

        // Spawn a detached background process to prevent terminal blocking
        const child = spawn(process.execPath, [__filename, 'run-analysis', branchName, jiraKey], {
            detached: true,
            stdio: 'ignore',
            windowsHide: true // Suppresses the CMD popup on Windows
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

        // Allow graph to initialize
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

        // Cleanup
        depwire.kill();

        // Trigger Windows desktop notification
        if (process.platform === 'win32') {
            spawn('powershell', ['-Command', `& {Add-Type -AssemblyName PresentationFramework; [System.Windows.MessageBox]::Show('Architectural blueprint for ${jiraKey} generated successfully!','Roo Code Automation')}`], { windowsHide: true });
        }
    } catch (err) {
        // Silently fail background tasks to avoid interrupting user workflow
    }
}

run();

```
### Step 4: Verify the Setup
To test that the daemon is working, open your terminal and check out a test branch with a valid Jira key:
```bash
git checkout -b feature/PROJ-123

```
Your terminal prompt should return to you immediately. Within about 30 to 60 seconds, a desktop notification will pop up, and you will see docs/analysis/PROJ-123-analysis.md appear in your IDE project tree.
*(Note: The repository's .gitignore has already been updated to ignore the .roo/ and docs/analysis/ folders, so you don't need to worry about accidentally pushing these files).*
Reach out if you hit any roadblocks with the CLI or environment variables.
Thanks,
[Your Name]
