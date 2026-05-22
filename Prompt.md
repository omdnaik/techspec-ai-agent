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
