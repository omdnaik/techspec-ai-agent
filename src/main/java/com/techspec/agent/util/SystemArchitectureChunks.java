import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SystemArchitectureChunkReader {

    public static void main(String[] args) {
        String jsonFilePath = "system_architecture_chunks.json"; // Update with actual path

        try {
            // Read and parse the JSON
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> chunks = mapper.readValue(
                    new File(jsonFilePath),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            // Print each chunk
            for (Map<String, Object> chunk : chunks) {
                int chunkId = (int) chunk.get("chunk_id");
                String text = (String) chunk.get("text");

                System.out.println("=== Chunk #" + chunkId + " ===");
                System.out.println(text);
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
