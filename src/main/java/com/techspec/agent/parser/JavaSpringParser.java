package com.techspec.agent.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class JavaSpringParser {

    private static final List<String> SPRING_COMPONENTS = List.of(
        "@Component", "@Service", "@Repository", "@Configuration"
    );

    public void parse(String sourceRoot, String outputFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode componentsArray = mapper.createArrayNode();

        List<Path> javaFiles = Files.walk(Paths.get(sourceRoot))
            .filter(p -> p.toString().endsWith(".java"))
            .collect(Collectors.toList());

        for (Path filePath : javaFiles) {
            List<String> lines = Files.readAllLines(filePath);
            boolean isSpringComponent = lines.stream()
                .anyMatch(line -> SPRING_COMPONENTS.stream().anyMatch(line::contains));

            if (isSpringComponent) {
                ObjectNode component = mapper.createObjectNode();
                component.put("file", filePath.toString());
                for (String annotation : SPRING_COMPONENTS) {
                    if (lines.stream().anyMatch(l -> l.contains(annotation))) {
                        component.put("type", annotation.replace("@", ""));
                        break;
                    }
                }

                String className = filePath.getFileName().toString().replace(".java", "");
                component.put("className", className);

                componentsArray.add(component);
            }
        }

        mapper.writerWithDefaultPrettyPrinter()
            .writeValue(new File(outputFilePath), componentsArray);
    }
}
