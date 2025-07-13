
package com.techspec.agent.extractor;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigMetadataExtractor {

    public static void extractConfigMetadata(File sqlDir, File outputJsonFile) throws IOException {
        List<Map<String, Object>> tableList = new ArrayList<>();

        File[] files = sqlDir.listFiles((dir, name) -> name.endsWith(".sql"));
        if (files == null) return;

        for (File file : files) {
            String content = Files.readString(file.toPath());

            // match CREATE TABLE statements
            Pattern createTablePattern = Pattern.compile("(?i)create table (\\w+)\\s*\\((.*?)\\)\\s*;", Pattern.DOTALL);
            Matcher createTableMatcher = createTablePattern.matcher(content);

            while (createTableMatcher.find()) {
                String tableName = createTableMatcher.group(1);
                if (!(tableName.toLowerCase().contains("config") || tableName.toLowerCase().contains("details"))) {
                    continue; // skip non-config/detail tables
                }
                String columnDefs = createTableMatcher.group(2);
                List<Map<String, String>> columns = new ArrayList<>();

                for (String line : columnDefs.split(",\\s*")) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 2) {
                        Map<String, String> col = new LinkedHashMap<>();
                        col.put("name", parts[0].replace("`", ""));
                        col.put("type", parts[1].toUpperCase());
                        columns.add(col);
                    }
                }

                Map<String, Object> tableEntry = new LinkedHashMap<>();
                tableEntry.put("tableName", tableName);
                tableEntry.put("columns", columns);
                tableEntry.put("sampleData", new ArrayList<String>());
                tableEntry.put("sampleInserts", new ArrayList<String>());
                tableList.add(tableEntry);
            }
        }

        // match INSERT INTO values
        for (File file : files) {
            String content = Files.readString(file.toPath());
            Pattern insertPattern = Pattern.compile("(?i)insert into (\\w+).*?values\\s*(\\(.*?\\))(?:;|$)", Pattern.DOTALL);
            Matcher insertMatcher = insertPattern.matcher(content);

            while (insertMatcher.find()) {
                String table = insertMatcher.group(1);
                if (!(table.toLowerCase().contains("config") || table.toLowerCase().contains("details"))) {
                    continue; // skip non-config/detail inserts
                }
                String values = insertMatcher.group(2).trim();

                for (Map<String, Object> tbl : tableList) {
                    if (tbl.get("tableName").toString().equalsIgnoreCase(table)) {
                        List<String> sampleData = (List<String>) tbl.get("sampleData");
                        sampleData.add(values);

                        List<String> sampleInserts = (List<String>) tbl.get("sampleInserts");
                        sampleInserts.add(insertMatcher.group(0).trim());
                    }
                }
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(outputJsonFile, tableList);
    }
}
