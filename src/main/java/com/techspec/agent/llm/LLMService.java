package com.techspec.agent.llm;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LLMService {

    private final String endpointUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    public LLMService(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String generateSpecFromFiles(Map<String, Path> contextFiles, String promptTemplate) throws IOException, InterruptedException {
        ObjectNode payload = mapper.createObjectNode();
        payload.put("prompt", promptTemplate);

        ObjectNode contextNode = payload.putObject("context");
        for (Map.Entry<String, Path> entry : contextFiles.entrySet()) {
            String name = entry.getKey();
            String content = Files.readString(entry.getValue());
            contextNode.put(name, content);
        }

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(endpointUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("LLM returned non-OK status: " + response.statusCode());
        }
    }
}
