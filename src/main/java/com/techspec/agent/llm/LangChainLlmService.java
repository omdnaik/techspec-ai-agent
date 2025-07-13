package com.techspec.agent.llm;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LangChainLlmService {

    private final ChatLanguageModel model;

    public LangChainLlmService() {
        String baseUrl = System.getenv().getOrDefault("LLM_BASE_URL", "http://localhost:11434/v1");
        String apiKey = System.getenv().getOrDefault("LLM_API_KEY", "ollama");
        String modelName = System.getenv().getOrDefault("LLM_MODEL", "mistral");

        this.model = OpenAiChatModel.builder()
            .baseUrl(baseUrl)
            .apiKey(apiKey)
            .modelName(modelName)
            .build();
    }

    public String generateSpecFromFiles(List<String> filePaths, String promptTemplate) throws IOException {
        StringBuilder contextBuilder = new StringBuilder();

        for (String file : filePaths) {
            contextBuilder.append("\n### File: ").append(file).append("\n");
            contextBuilder.append(Files.readString(Path.of(file))).append("\n\n");
        }

        String finalPrompt = promptTemplate.replace("{{context}}", contextBuilder.toString());
        return model.generate(finalPrompt);
    }

    public String generate(String prompt) {
        return model.generate(prompt);
    }
}
