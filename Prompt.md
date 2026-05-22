We are converting this application into a lean, strict MCP server that uses Tree-sitter and Neo4j. We are abandoning all built-in LLM chat and semantic/vector search features.
​Please execute the following refactoring:
​The Purge: Ruthlessly delete all files, classes, and dependencies related to:
​LLM clients (OpenAI, Gemini), API key validations, and prompt generation (e.g., the llm directory).
​Semantic search, embeddings, UniXcoder, and vector stores.
​Remove all AI/ML libraries from pyproject.toml or requirements.txt.
​Startup Ingestion: We want the application to automatically ingest the codebase into Neo4j when the MCP server starts.
​Modify the mcp command in the CLI (cli.py) to accept a --repo-path argument.
​Before starting the MCP server's stdio loop, the mcp command must initialize the database adapter, instantiate the ingestor, and run the Tree-sitter ingestion process on the provided --repo-path.
​Only after the ingestion is complete should it call the mcp_server.run() or equivalent method to begin listening for Roo Code.
​Do not alter the Neo4j database connection strings or the core Tree-sitter logic we established earlier.







We need to make our graph database a living reflection of the codebase. Please implement a background file watcher that automatically triggers the Tree-sitter ingestion process whenever the codebase changes.
​Please execute the following:
​Add the watchdog library to our dependencies.
​Create a new service (e.g., file_watcher.py) that monitors the --repo-path directory.
​The watcher should only care about events (modified, created, deleted) for .java, .xml, .properties, and .yml files.
​Crucial Requirement: Implement a debounce mechanism (e.g., 2-3 seconds). IDEs auto-save frequently, and we do not want to trigger the parser 50 times a minute. Only trigger the ingestion function after the file modification events have paused.
​Wire this watcher to start as a background daemon thread inside the mcp CLI command right after the initial startup ingestion completes, but before the MCP server's stdio communication loop begins.
​Ensure this background process is thread-safe and does not block the main MCP server from listening to incoming commands.







We are swapping out Memgraph for a local, embedded Neo4j database running on bolt://localhost:7687 with authentication disabled.
​Please refactor the codebase to completely remove the mgclient dependency and replace it with the official neo4j Python package (from neo4j import GraphDatabase).
​Specifically, update the following:
​In codebase_rag/services/graph_service.py (MemgraphIngestor class): Update the _create_connection() and context manager logic to use GraphDatabase.driver("bolt://localhost:7687", auth=None). Ensure any cursor.execute() calls are updated to the Neo4j session.run() syntax.
​In codebase_rag/tools/health_checker.py: Update check_memgraph_connection() to ping the Neo4j driver instead.
​Maintain the existing thread-safety and context manager patterns. Do not change the actual Cypher queries, only the driver connection and execution syntax
