package com.techspec.agent.extractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techspec.agent.util.SqlDDLExtractor;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ConfigMetadataExtractor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void extract(File ddlFolder, File outputFile) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();

        for (File file : Objects.requireNonNull(ddlFolder.listFiles())) {
            String content = Files.readString(file.toPath());

            // 🔹 Extract both regular and embedded CREATE TABLE DDLs
            List<String> allCreateStatements = new ArrayList<>();
            allCreateStatements.addAll(SqlDDLExtractor.extractCreateStatements(content));

            for (String ddl : allCreateStatements) {
                Statement stmt = safeParse(ddl);
                if (stmt instanceof CreateTable createTable) {
                    String tableName = createTable.getTable().getName().toLowerCase();
                    if (tableName.contains("config") || tableName.contains("detail")) {
                        Map<String, Object> tableMap = new LinkedHashMap<>();
                        tableMap.put("tableName", tableName);

                        List<Map<String, String>> columns = new ArrayList<>();
                        for (ColumnDefinition column : createTable.getColumnDefinitions()) {
                            Map<String, String> colMap = new LinkedHashMap<>();
                            colMap.put("name", column.getColumnName());
                            colMap.put("type", column.getColDataType().toString());
                            columns.add(colMap);
                        }

                        tableMap.put("columns", columns);
                        result.add(tableMap);
                    }
                }
            }

            // 🔹 Append insert samples
            Map<String, List<String>> insertMap = SqlDDLExtractor.extractInsertSamples(content);
            for (Map<String, Object> table : result) {
                String name = (String) table.get("tableName");
                table.put("sampleData", insertMap.getOrDefault(name.toLowerCase(), new ArrayList<>()));
            }
        }

        try (FileWriter writer = new FileWriter(outputFile)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, result);
        }
    }

    private Statement safeParse(String ddl) {
        try {
            return CCJSqlParserUtil.parse(ddl);
        } catch (Exception e) {
            System.err.println("Failed to parse DDL: " + ddl);
            return null;
        }
    }
}
