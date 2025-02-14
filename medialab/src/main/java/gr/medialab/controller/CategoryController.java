package gr.medialab.controller;

import gr.medialab.model.Category;
import gr.medialab.service.JsonCategoryManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for managing categories in the application.
 * Handles creation, modification, deletion and retrieval of categories.
 */
public class CategoryController {
    // List to store all categories in memory
    private final List<Category> categories;
    // Manager for JSON persistence operations
    private final JsonCategoryManager jsonManager;
    // Reference to TaskController for handling task-related operations
    private final TaskController taskController;
    // Constant for the JSON file name where categories are stored
    private static final String CATEGORIES_FILE = "categories.json";

    /**
     * Constructor initializes the controller with necessary dependencies
     * @param taskController The task controller for managing related tasks
     */
    public CategoryController(TaskController taskController) {
        this.taskController = taskController;
        this.jsonManager = new JsonCategoryManager();
        this.categories = loadCategories();
    }

    /**
     * Loads categories from JSON file
     * @return List of categories, or empty list if none exist
     */
    public List<Category> loadCategories() {
        List<Category> loadedCategories = jsonManager.loadCategories(CATEGORIES_FILE);
        return loadedCategories != null ? loadedCategories : new ArrayList<>();
    }

    /**
     * Saves current categories to JSON file
     */
    public void saveCategories() {
        jsonManager.saveCategories(categories, CATEGORIES_FILE);
    }

    /**
     * Adds a new category with the specified name
     * @param name The name for the new category
     * @throws IllegalArgumentException if a category with the same name already exists
     */
    public void addCategory(String name) {
        // Check if category with same name exists
        if (getCategoryByName(name) != null) {
            throw new IllegalArgumentException("Category with this name already exists");
        }
        categories.add(new Category(name));
        saveCategories();
    }

    /**
     * Updates the name of an existing category
     * @param category The category to update
     * @param newName The new name for the category
     * @throws IllegalArgumentException if another category already has the new name
     */
    public void updateCategory(Category category, String newName) {
        // Check if new name already exists in another category
        Category existingCategory = getCategoryByName(newName);
        if (existingCategory != null && !existingCategory.getId().equals(category.getId())) {
            throw new IllegalArgumentException("Category with this name already exists");
        }
        
        Category categoryToUpdate = getCategoryById(category.getId());
        if (categoryToUpdate != null) {
            categoryToUpdate.setName(newName);
            saveCategories();
        }
    }

    /**
     * Deletes a category and all its associated tasks
     * This will also trigger deletion of any reminders associated with the tasks
     * @param category The category to delete
     */
    public void deleteCategory(Category category) {
        // First delete all tasks in this category
        taskController.deleteCategoryTasks(category);
        
        // Then remove the category itself
        categories.remove(category);
        saveCategories();
    }

    /**
     * Retrieves all categories
     * @return A new list containing all categories
     */
    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }

    /**
     * Finds a category by its ID
     * @param id The ID of the category to find
     * @return The category if found, null otherwise
     */
    public Category getCategoryById(String id) {
        return categories.stream()
                .filter(category -> category.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a category by its name
     * @param name The name of the category to find
     * @return The category if found, null otherwise
     */
    public Category getCategoryByName(String name) {
        return categories.stream()
                .filter(category -> category.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
