package com.techspec.agent.extractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PropertiesExtractor {

    public static void extractToJson(Path propertiesFile, Path outputFile) throws IOException {
        Properties props = new Properties();

        try (InputStream input = Files.newInputStream(propertiesFile)) {
            props.load(input);
        }

        Map<String, String> flatMap = new HashMap<>();
        for (String name : props.stringPropertyNames()) {
            flatMap.put(name, props.getProperty(name));
        }

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(outputFile.toFile(), flatMap);

        System.out.println("✅ Extracted: " + propertiesFile.getFileName() + " ➝ " + outputFile);
    }
}
