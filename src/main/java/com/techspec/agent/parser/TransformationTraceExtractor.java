package com.techspec.agent.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TransformationTraceExtractor {

    private static final Pattern MAPPING_PATTERN = Pattern.compile("(\w+)\.set(\w+)\\((\w+)\.get(\w+)\\(\\)\\);");
    private final ObjectMapper mapper = new ObjectMapper();

    public void extractTrace(String sourceRoot, String outputFilePath) throws IOException {
        ArrayNode mappings = mapper.createArrayNode();

        List<Path> javaFiles = Files.walk(Paths.get(sourceRoot))
            .filter(p -> p.toString().endsWith(".java"))
            .collect(Collectors.toList());

        for (Path filePath : javaFiles) {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                Matcher matcher = MAPPING_PATTERN.matcher(line.replaceAll("\\s+", ""));
                if (matcher.find()) {
                    ObjectNode mapping = mapper.createObjectNode();
                    mapping.put("fromObject", matcher.group(3));
                    mapping.put("fromField", matcher.group(4));
                    mapping.put("toObject", matcher.group(1));
                    mapping.put("toField", matcher.group(2));
                    mapping.put("file", filePath.toString());
                    mappings.add(mapping);
                }
            }
        }

        mapper.writerWithDefaultPrettyPrinter()
            .writeValue(Paths.get(outputFilePath).toFile(), mappings);
    }
}
