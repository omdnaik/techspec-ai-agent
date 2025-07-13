package com.techspec.agent.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SQLSchemaParser {

    private final ObjectMapper mapper = new ObjectMapper();

    public void parseDDL(String sqlFolderPath, String outputFilePath) throws IOException {
        ArrayNode tablesArray = mapper.createArrayNode();

        Files.walk(Paths.get(sqlFolderPath))
            .filter(p -> p.toString().endsWith(".sql"))
            .forEach(path -> {
                try {
                    String sql = Files.readString(path);
                    List<Statement> statements = CCJSqlParserUtil.parseStatements(sql).getStatements();

                    for (Statement stmt : statements) {
                        if (stmt instanceof CreateTable createTable) {
                            ObjectNode tableNode = mapper.createObjectNode();
                            tableNode.put("tableName", createTable.getTable().getName());

                            ArrayNode columns = mapper.createArrayNode();
                            List<ColumnDefinition> columnDefs = createTable.getColumnDefinitions();

                            if (columnDefs != null) {
                                for (ColumnDefinition col : columnDefs) {
                                    ObjectNode colNode = mapper.createObjectNode();
                                    colNode.put("name", col.getColumnName());
                                    colNode.put("type", col.getColDataType().getDataType());
                                    columns.add(colNode);
                                }
                            }
                            tableNode.set("columns", columns);
                            tablesArray.add(tableNode);
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Failed to parse " + path + ": " + e.getMessage());
                }
            });

        mapper.writerWithDefaultPrettyPrinter()
            .writeValue(new File(outputFilePath), tablesArray);
    }
}
