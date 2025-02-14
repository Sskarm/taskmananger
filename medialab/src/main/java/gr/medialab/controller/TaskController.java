package gr.medialab.controller;

import gr.medialab.model.Task;
import gr.medialab.model.Category;
import gr.medialab.model.Priority;
import gr.medialab.model.enums.TaskStatus;
import gr.medialab.service.JsonTaskManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskController {
    // List to store all tasks in memory
    private final List<Task> tasks;
    // Manager for JSON persistence operations
    private final JsonTaskManager jsonManager;
    // Constant for the JSON file name where tasks are stored
    private static final String TASKS_FILE = "tasks.json";

    public TaskController() {
        this.jsonManager = new JsonTaskManager();
        this.tasks = loadTasks();
        checkOverdueTasks(); // Check for overdue tasks on startup
    }

    public List<Task> loadTasks() {
        List<Task> loadedTasks = jsonManager.loadTasks(TASKS_FILE);
        return loadedTasks != null ? loadedTasks : new ArrayList<>();
    }

    private void checkOverdueTasks() {
        boolean needsSave = false;
        for (Task task : tasks) {
            if (task.isOverdue()) {
                task.updateStatusIfOverdue();
                needsSave = true;
            }
        }
        if (needsSave) {
            saveTasks();
        }
    }

    public void saveTasks() {
        jsonManager.saveTasks(tasks, TASKS_FILE);
    }

    // Add new task
    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();
    }

    // Update existing task
    public void updateTask(Task task) {
        Task existingTask = getTaskById(task.getId());
        if (existingTask != null) {
            // Update all fields
            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setCategory(task.getCategory());
            existingTask.setPriority(task.getPriority());
            existingTask.setDeadline(task.getDeadline());
            existingTask.setStatus(task.getStatus());
            
            // Check if task is overdue after update
            existingTask.updateStatusIfOverdue();
            
            saveTasks();
        }
    }

    // Delete task
    public void deleteTask(Task task) {
        tasks.remove(task);
        saveTasks();
    }
    
    // Get task by ID
    public Task getTaskById(String id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getTasksByCategory(Category category) {
        return tasks.stream()
                .filter(task -> task.getCategory().equals(category))
                .toList();
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return tasks.stream()
                .filter(task -> task.getStatus().equals(status))
                .toList();
    }

    public List<Task> getTasksByDeadline(LocalDate deadline) {
        return tasks.stream()
                .filter(task -> task.getDeadline().equals(deadline))
                .toList();
    }

    public List<Task> getUpcomingTasks(int days) {
        LocalDate threshold = LocalDate.now().plusDays(days);
        return tasks.stream()
                .filter(task -> !task.isCompleted())
                .filter(task -> !task.getDeadline().isAfter(threshold))
                .toList();
    }

    public List<Task> getOverdueTasks() {
        LocalDate today = LocalDate.now();
        return tasks.stream()
                .filter(task -> !task.isCompleted())
                .filter(task -> task.getDeadline().isBefore(today))
                .toList();
    }

    public List<Task> getCompletedTasks() {
        return tasks.stream()
                .filter(Task::isCompleted)
                .toList();
    }

    public List<Task> getDelayedTasks() {
        return tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DELAYED)
                .toList();
    }

    public void deleteCategoryTasks(Category category) {
        tasks.removeIf(task -> task.getCategory().equals(category));
        saveTasks();
    }

    public void reassignTasksPriority(Priority oldPriority, Priority newPriority) {
        getAllTasks().stream()
                .filter(task -> task.getPriority().equals(oldPriority))
                .forEach(task -> {
                    task.setPriority(newPriority);
                    updateTask(task);
                });
    }

    public List<Task> searchTasks(String title, Category category, Priority priority) {
        return getAllTasks().stream()
            .filter(task -> {
                // Title filter
                boolean titleMatch = title == null || title.trim().isEmpty() ||
                                   task.getTitle().toLowerCase().contains(title.toLowerCase());
                
                // Category filter
                boolean categoryMatch = category == null ||
                                      (task.getCategory() != null && task.getCategory().equals(category));
                
                // Priority filter
                boolean priorityMatch = priority == null ||
                                      (task.getPriority() != null && task.getPriority().equals(priority));
                
                return titleMatch && categoryMatch && priorityMatch;
            })
            .collect(Collectors.toList());
    }    
}
