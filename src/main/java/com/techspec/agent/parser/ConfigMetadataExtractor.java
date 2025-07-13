package com.techspec.agent.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ConfigMetadataExtractor {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Set<String> configTableNames = new HashSet<>();
    private final Map<String, ArrayNode> sampleData = new HashMap<>();

    public void extract(String sqlFolderPath, String outputFilePath) throws IOException {
        ArrayNode configTables = mapper.createArrayNode();

        Files.walk(Paths.get(sqlFolderPath))
            .filter(p -> p.toString().endsWith(".sql"))
            .forEach(path -> {
                try {
                    String sql = Files.readString(path);
                    List<Statement> statements = CCJSqlParserUtil.parseStatements(sql).getStatements();

                    for (Statement stmt : statements) {
                        if (stmt instanceof CreateTable ct) {
                            String tableName = ct.getTable().getName().toLowerCase();
                            if (tableName.contains("config") || tableName.contains("params") || tableName.contains("settings")) {
                                configTableNames.add(tableName);

                                ObjectNode tableNode = mapper.createObjectNode();
                                tableNode.put("tableName", tableName);

                                ArrayNode columns = mapper.createArrayNode();
                                List<ColumnDefinition> columnDefs = ct.getColumnDefinitions();
                                if (columnDefs != null) {
                                    for (ColumnDefinition col : columnDefs) {
                                        ObjectNode colNode = mapper.createObjectNode();
                                        colNode.put("name", col.getColumnName());
                                        colNode.put("type", col.getColDataType().getDataType());
                                        columns.add(colNode);
                                    }
                                }
                                tableNode.set("columns", columns);
                                tableNode.set("sampleData", sampleData.getOrDefault(tableName, mapper.createArrayNode()));
                                configTables.add(tableNode);
                            }
                        }

                        if (stmt instanceof Insert insert) {
                            String targetTable = insert.getTable().getName().toLowerCase();
                            if (configTableNames.contains(targetTable)) {
                                ArrayNode rows = sampleData.computeIfAbsent(targetTable, k -> mapper.createArrayNode());
                                insert.getItemsList().getExpressions().stream()
                                    .limit(3) // only first few rows for brevity
                                    .forEach(expr -> rows.add(expr.toString()));
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to parse " + path + ": " + e.getMessage());
                }
            });

        mapper.writerWithDefaultPrettyPrinter()
            .writeValue(new File(outputFilePath), configTables);
    }
}
