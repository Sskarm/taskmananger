package gr.medialab.service;


import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import gr.medialab.model.Priority;

public class JsonPriorityManager {
    private static final String DATA_DIR = "src/main/resources/medialab";

    public JsonPriorityManager() {
        ensureDataDirectory();
    }

    private void ensureDataDirectory() {
        try {
            Path dirPath = Paths.get(DATA_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                System.out.println("Created directory: " + dirPath.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create data directory: " + DATA_DIR, e);
        }
    }

    public void savePriorities(List<Priority> priorities, String filename) {
        Path filePath = Paths.get(DATA_DIR, filename);
        
        Map<String, Object> config = Map.of(JsonGenerator.PRETTY_PRINTING, true);
        JsonBuilderFactory factory = Json.createBuilderFactory(config);
        
        JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();

        for (Priority priority : priorities) {
            JsonObjectBuilder priorityBuilder = factory.createObjectBuilder()
                .add("id", priority.getId())
                .add("name", priority.getName());
            
            arrayBuilder.add(priorityBuilder);
        }

        JsonObject wrapper = factory.createObjectBuilder()
            .add("data", arrayBuilder)
            .build();

        try (OutputStream os = Files.newOutputStream(filePath);
             JsonWriter writer = Json.createWriter(os)) {
            writer.writeObject(wrapper);
        } catch (IOException e) {
            throw new RuntimeException("Error saving to " + filename, e);
        }
    }

    public List<Priority> loadPriorities(String filename) {
        Path filePath = Paths.get(DATA_DIR, filename);

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (InputStream is = Files.newInputStream(filePath);
             JsonReader reader = Json.createReader(is)) {
            
            JsonObject obj = reader.readObject();
            JsonArray results = obj.getJsonArray("data");
            
            List<Priority> priorities = new ArrayList<>();

            for (JsonValue value : results) {
                JsonObject result = value.asJsonObject();
                Priority priority = new Priority();
                priority.setId(result.getString("id"));
                priority.setName(result.getString("name"));
                priorities.add(priority);
            }
            return priorities;
        } catch (IOException e) {
            throw new RuntimeException("Error loading from " + filename, e);
        }
    }
}