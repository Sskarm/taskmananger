package gr.medialab.controller;

import gr.medialab.model.Priority;
import gr.medialab.service.JsonPriorityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for managing priorities in the application.
 * Handles creation, modification, deletion and retrieval of priorities.
 */
public class PriorityController {
    private final List<Priority> priorities;
    private final JsonPriorityManager jsonManager;
    private final TaskController taskController;
    private static final String PRIORITIES_FILE = "priorities.json";
    private static final String DEFAULT_PRIORITY_NAME = "Default";

    /**
     * Constructor initializes the controller with necessary dependencies
     * @param taskController The task controller for managing related tasks
     */
    public PriorityController(TaskController taskController) {
        this.taskController = taskController;
        this.jsonManager = new JsonPriorityManager();
        this.priorities = loadPriorities();
        ensureDefaultPriority();
    }

    /**
     * Loads priorities from JSON file
     * @return List of priorities, or empty list if none exist
     */
    public List<Priority> loadPriorities() {
        List<Priority> loadedPriorities = jsonManager.loadPriorities(PRIORITIES_FILE);
        return loadedPriorities != null ? loadedPriorities : new ArrayList<>();
    }

    /**
     * Saves current priorities to JSON file
     */
    public void savePriorities() {
        jsonManager.savePriorities(priorities, PRIORITIES_FILE);
    }

    /**
     * Ensures that the default priority exists
     */
    private void ensureDefaultPriority() {
        if (getPriorityByName(DEFAULT_PRIORITY_NAME) == null) {
            priorities.add(new Priority(DEFAULT_PRIORITY_NAME));
            savePriorities();
        }
    }

    /**
     * Adds a new priority with the specified name
     * @param name The name for the new priority
     * @throws IllegalArgumentException if a priority with the same name already exists
     */
    public void addPriority(String name) {
        if (getPriorityByName(name) != null) {
            throw new IllegalArgumentException("Priority with this name already exists");
        }
        priorities.add(new Priority(name));
        savePriorities();
    }

    /**
     * Updates the name of an existing priority
     * @param priority The priority to update
     * @param newName The new name for the priority
     * @throws IllegalArgumentException if another priority already has the new name or if trying to rename the default priority
     */
    public void updatePriority(Priority priority, String newName) {
        if (priority.getName().equals(DEFAULT_PRIORITY_NAME)) {
            throw new IllegalArgumentException("Cannot rename the default priority");
        }
        Priority existingPriority = getPriorityByName(newName);
        if (existingPriority != null && !existingPriority.getId().equals(priority.getId())) {
            throw new IllegalArgumentException("Priority with this name already exists");
        }
        
        Priority priorityToUpdate = getPriorityById(priority.getId());
        if (priorityToUpdate != null) {
            priorityToUpdate.setName(newName);
            savePriorities();
        }
    }

    /**
     * Deletes a priority and reassigns all its tasks to the default priority
     * @param priority The priority to delete
     * @throws IllegalArgumentException if trying to delete the default priority
     */
    public void deletePriority(Priority priority) {
        if (priority.getName().equals(DEFAULT_PRIORITY_NAME)) {
            throw new IllegalArgumentException("Cannot delete the default priority");
        }
        
        Priority defaultPriority = getPriorityByName(DEFAULT_PRIORITY_NAME);
        taskController.reassignTasksPriority(priority, defaultPriority);
        
        priorities.remove(priority);
        savePriorities();
    }

    /**
     * Retrieves all priorities
     * @return A new list containing all priorities
     */
    public List<Priority> getAllPriorities() {
        return new ArrayList<>(priorities);
    }

    /**
     * Finds a priority by its ID
     * @param id The ID of the priority to find
     * @return The priority if found, null otherwise
     */
    public Priority getPriorityById(String id) {
        return priorities.stream()
                .filter(priority -> priority.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a priority by its name
     * @param name The name of the priority to find
     * @return The priority if found, null otherwise
     */
    public Priority getPriorityByName(String name) {
        return priorities.stream()
                .filter(priority -> priority.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the default priority
     * @return The default priority
     */
    public Priority getDefaultPriority() {
        return getPriorityByName(DEFAULT_PRIORITY_NAME);
    }
}
