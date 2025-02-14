package gr.medialab.service;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import gr.medialab.model.Task;
import gr.medialab.model.Category;
import gr.medialab.model.Priority;
import gr.medialab.model.enums.TaskStatus;

public class JsonTaskManager {
    //define the directory where the json files are stored
    private static final String DATA_DIR = "src/main/resources/medialab";

    //constructor: creates a new JsonTaskManager and ensures the data directory exists 
    public JsonTaskManager() {
        ensureDataDirectory();
    }

    //check if the dir exists if not it creates it. called when JsonTaskManager is started
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

    //saves a list of tasks in json
    //@param tasks list of tasks
    //@param filename , name of the file it will be stored
    public void saveTasks(List<Task> tasks, String filename) {
        //full path for the file
        Path filePath = Paths.get(DATA_DIR, filename);
        
        //json format to be readable
        Map<String, Object> config = Map.of(JsonGenerator.PRETTY_PRINTING, true);
        JsonBuilderFactory factory = Json.createBuilderFactory(config);
        
        //create jason array to hold all tasks
        JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();

        //Converts each task to Json format
        for (Task task : tasks) {
            JsonObjectBuilder taskBuilder = factory.createObjectBuilder()
                .add("id", task.getId())
                .add("title", task.getTitle())
                .add("description", task.getDescription())
                .add("status", task.getStatus().toString())//enum -> string
                .add("deadline", task.getDeadline().toString())//Localdate -> string
                .add("priority", task.getPriority().getName())
                .add("category", task.getCategory().getName());
            
            //add the Json object tasks to the array
            arrayBuilder.add(taskBuilder);
        }

        //Wrap the array in a container object with "data" key
        JsonObject wrapper = factory.createObjectBuilder()
            .add("data", arrayBuilder)
            .build();

        //Write json to file
        try (OutputStream os = Files.newOutputStream(filePath);
             JsonWriter writer = Json.createWriter(os)) {
            writer.writeObject(wrapper);
        } catch (IOException e) {
            throw new RuntimeException("Error saving to " + filename, e);
        }
    }

    //Loads tasks from json file
    //@param filename, name to load from
    //@return List of task objects loaded from file
    public List<Task> loadTasks(String filename) {
        //Create full filepath
        Path filePath = Paths.get(DATA_DIR, filename);

        //If the file doesnt exist, return empty list
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (InputStream is = Files.newInputStream(filePath);
             JsonReader reader = Json.createReader(is)) {
            
            //Read the root object and get the "data" array
            JsonObject obj = reader.readObject();
            JsonArray results = obj.getJsonArray("data");
            
            List<Task> tasks = new ArrayList<>();

            //process each task in the json array
            for (JsonValue value : results){
                JsonObject result = value.asJsonObject(); 
                Task task = new Task();//create new default task

                // Set all the task properties from json
                task.setId(result.getString("id"));
                task.setTitle(result.getString("title"));
                task.setDescription(result.getString("description"));
                task.setStatus(TaskStatus.valueOf(result.getString("status")));
                task.setDeadline(LocalDate.parse(result.getString("deadline")));
                
                // Create new Priority and Category objects
                //Since ValueOf is not defined
                Priority priority = new Priority(result.getString("priority"));
                Category category = new Category(result.getString("category"));
            
                task.setPriority(priority);
                task.setCategory(category);
                
            //add completed task to the list
            tasks.add(task);
            }
            return tasks;
        } catch (IOException e) {
            throw new RuntimeException("Error loading from " + filename, e);
        }
    }
}
