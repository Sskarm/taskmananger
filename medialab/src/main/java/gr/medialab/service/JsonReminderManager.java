package gr.medialab.service;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import gr.medialab.model.Reminder;
import gr.medialab.model.enums.ReminderType;
import gr.medialab.model.Task;
import gr.medialab.controller.TaskController;

public class JsonReminderManager {
    private static final String DATA_DIR = "src/main/resources/medialab";
    private final TaskController taskController;

    public JsonReminderManager(TaskController taskController) {
        this.taskController = taskController;
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

    public void saveReminders(List<Reminder> reminders, String filename) {
        Path filePath = Paths.get(DATA_DIR, filename);
        
        Map<String, Object> config = Map.of(JsonGenerator.PRETTY_PRINTING, true);
        JsonBuilderFactory factory = Json.createBuilderFactory(config);
        
        JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();

        for (Reminder reminder : reminders) {
            JsonObjectBuilder reminderBuilder = factory.createObjectBuilder()
                .add("id", reminder.getId())
                .add("taskId", reminder.getTask().getId())
                .add("type", reminder.getType().toString())
                .add("reminderDate", reminder.getReminderDate().toString());
            
            arrayBuilder.add(reminderBuilder);
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

    public List<Reminder> loadReminders(String filename) {
        Path filePath = Paths.get(DATA_DIR, filename);

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (InputStream is = Files.newInputStream(filePath);
             JsonReader reader = Json.createReader(is)) {
            
            JsonObject obj = reader.readObject();
            JsonArray results = obj.getJsonArray("data");
            
            List<Reminder> reminders = new ArrayList<>();

            for (JsonValue value : results) {
                JsonObject result = value.asJsonObject();
                
                // Get the associated task using taskController
                String taskId = result.getString("taskId");
                Task task = taskController.getTaskById(taskId);
                
                if (task != null) {
                    ReminderType type = ReminderType.valueOf(result.getString("type"));
                    LocalDate reminderDate = LocalDate.parse(result.getString("reminderDate"));
                    
                    Reminder reminder = new Reminder(task, type, reminderDate);
                    reminders.add(reminder);
                }
            }
            return reminders;
        } catch (IOException e) {
            throw new RuntimeException("Error loading from " + filename, e);
        }
    }
}

