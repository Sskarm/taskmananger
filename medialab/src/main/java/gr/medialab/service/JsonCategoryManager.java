package gr.medialab.service;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import gr.medialab.model.Category;

public class JsonCategoryManager {
    private static final String DATA_DIR = "src/main/resources/medialab";

    public JsonCategoryManager() {
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

    public void saveCategories(List<Category> categories, String filename) {
        Path filePath = Paths.get(DATA_DIR, filename);
        
        Map<String, Object> config = Map.of(JsonGenerator.PRETTY_PRINTING, true);
        JsonBuilderFactory factory = Json.createBuilderFactory(config);
        
        JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();

        for (Category category : categories) {
            JsonObjectBuilder categoryBuilder = factory.createObjectBuilder()
                .add("id", category.getId())
                .add("name", category.getName());
            
            arrayBuilder.add(categoryBuilder);
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

    public List<Category> loadCategories(String filename) {
        Path filePath = Paths.get(DATA_DIR, filename);

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (InputStream is = Files.newInputStream(filePath);
             JsonReader reader = Json.createReader(is)) {
            
            JsonObject obj = reader.readObject();
            JsonArray results = obj.getJsonArray("data");
            
            List<Category> categories = new ArrayList<>();

            for (JsonValue value : results) {
                JsonObject result = value.asJsonObject();
                Category category = new Category();
                category.setId(result.getString("id"));
                category.setName(result.getString("name"));
                categories.add(category);
            }
            return categories;
        } catch (IOException e) {
            throw new RuntimeException("Error loading from " + filename, e);
        }
    }
}