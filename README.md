# TechSpec AI Agent


import dev.langchain4j.model.localai.LocalAiChatModel;
import dev.langchain4j.model.localai.LocalAiChatModel.RequestOptions;

import java.util.Map;

RequestOptions requestOptions = RequestOptions.builder()
    .verifySsl(false)
    .extraBodyProperties(Map.of(
        "temperature", 0.7,
        "top_p", 0.9,
        "stop", new String[] {"###"}  // stop sequences
    ))
    .build();

LocalAiChatModel model = LocalAiChatModel.builder()
    .baseUrl("http://localhost:8080")
    .modelName("mistral")
    .requestOptions(requestOptions)
    .build();

This project analyzes Java Spring Boot code, SQL DDLs, and generates technical documentation in Markdown format.
