package com.techspec.agent;

import com.techspec.agent.llm.LLMService;
import com.techspec.agent.llm.PromptTemplates;
import com.techspec.agent.parser.ConfigMetadataExtractor;
import com.techspec.agent.parser.JavaSpringParser;
import com.techspec.agent.parser.SQLSchemaParser;
import com.techspec.agent.util.MarkdownExporter;

import java.nio.file.Path;
import java.util.Map;

public class CliRunner {

    public static void main(String[] args) {
        String sourceDir = null;
        String sqlDir = null;
        String outputDir = "./output";

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--src": sourceDir = args[++i]; break;
                case "--sql": sqlDir = args[++i]; break;
                case "--out": outputDir = args[++i]; break;
            }
        }

        if (sourceDir == null || sqlDir == null) {
            System.err.println("Usage: java -jar techspec-ai-agent.jar --src ./src --sql ./sql --out ./output");
            return;
        }

        try {
            System.out.println("🔍 Extracting DI components...");
            new JavaSpringParser().parse(sourceDir, outputDir + "/di_components.json");

            System.out.println("🗄️  Parsing DB schema...");
            new SQLSchemaParser().parseDDL(sqlDir, outputDir + "/db_schema.json");

            System.out.println("⚙️  Parsing config metadata...");
            new ConfigMetadataExtractor().extract(sqlDir, outputDir + "/config_metadata.json");

            System.out.println("🤖 Invoking LLM...");
            LLMService llm = new LLMService("http://localhost:11434/v1/generate");
            Map<String, Path> contextFiles = Map.of(
                "di_components.json", Path.of(outputDir, "di_components.json"),
                "db_schema.json", Path.of(outputDir, "db_schema.json"),
                "config_metadata.json", Path.of(outputDir, "config_metadata.json")
            );

            String markdown = llm.generateSpecFromFiles(contextFiles, PromptTemplates.MASTER_SPEC_PROMPT);
            MarkdownExporter.exportToFile(markdown, outputDir + "/master_techspec.md");
            MarkdownExporter.exportAsHtml(markdown, outputDir + "/master_techspec.html")

            System.out.println("✅ All tasks completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
