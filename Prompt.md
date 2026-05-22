
We are swapping out Memgraph for a local, embedded Neo4j database running on bolt://localhost:7687 with authentication disabled.
​Please refactor the codebase to completely remove the mgclient dependency and replace it with the official neo4j Python package (from neo4j import GraphDatabase).
​Specifically, update the following:
​In codebase_rag/services/graph_service.py (MemgraphIngestor class): Update the _create_connection() and context manager logic to use GraphDatabase.driver("bolt://localhost:7687", auth=None). Ensure any cursor.execute() calls are updated to the Neo4j session.run() syntax.
​In codebase_rag/tools/health_checker.py: Update check_memgraph_connection() to ping the Neo4j driver instead.
​Maintain the existing thread-safety and context manager patterns. Do not change the actual Cypher queries, only the driver connection and execution syntax
