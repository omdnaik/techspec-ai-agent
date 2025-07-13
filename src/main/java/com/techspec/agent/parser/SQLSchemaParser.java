package com.techspec.agent.extractor;

import com.techspec.agent.model.TableSchema;
import com.techspec.agent.util.SqlDDLExtractor;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class SqlSchemaParser {

    public List<TableSchema> parse(File ddlFolder) throws IOException {
        List<TableSchema> allSchemas = new ArrayList<>();

        for (File file : Objects.requireNonNull(ddlFolder.listFiles())) {
            String content = Files.readString(file.toPath());

            // Extract regular DDLs
            List<Statement> statements = Arrays.stream(content.split(";"))
                .map(String::trim)
                .filter(s -> s.toUpperCase().startsWith("CREATE TABLE"))
                .map(this::safeParse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            for (Statement stmt : statements) {
                if (stmt instanceof CreateTable createTable) {
                    allSchemas.add(TableSchema.from(createTable));
                }
            }

            // Extract embedded DDLs from PL/SQL blocks
            List<String> embeddedDdls = SqlDDLExtractor.extractCreateStatements(content);
            for (String ddl : embeddedDdls) {
                Statement stmt = safeParse(ddl);
                if (stmt instanceof CreateTable createTable) {
                    allSchemas.add(TableSchema.from(createTable));
                }
            }
        }

        return allSchemas;
    }

    private Statement safeParse(String ddl) {
        try {
            return CCJSqlParserUtil.parse(ddl);
        } catch (Exception e) {
            System.err.println("Failed to parse: " + ddl);
            return null;
        }
    }
}
