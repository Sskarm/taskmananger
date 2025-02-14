package gr.medialab;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import gr.medialab.controller.*;
import gr.medialab.model.Category;
import gr.medialab.model.Priority;
import gr.medialab.model.Task;
import gr.medialab.model.Reminder;
import gr.medialab.model.enums.ReminderType;
import gr.medialab.model.enums.TaskStatus;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

public class MediaLabAssistant extends Application {
    private static TaskController taskController;
    private static CategoryController categoryController;
    private static PriorityController priorityController;
    private static ReminderController reminderController;

    // UI Components
    private TableView<Task> taskTable;
    private TableView<Category> categoryTable;
    private TableView<Priority> priorityTable;
    private TableView<Reminder> reminderTable;
    
    // Statistics Labels
    private Label totalTasksLabel;
    private Label completedTasksLabel;
    private Label delayedTasksLabel;
    private Label upcomingTasksLabel;

    @Override
    public void init() {
        initializeControllers();
        loadInitialData();
        checkDelayedTasks();
    }

    private void initializeControllers() {
        taskController = new TaskController();
        categoryController = new CategoryController(taskController);
        priorityController = new PriorityController(taskController);
        reminderController = new ReminderController(taskController);
    }

    private void loadInitialData() {
        taskController.loadTasks();
        categoryController.loadCategories();
        priorityController.loadPriorities();
        reminderController.loadReminders();
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
        
        // Create top statistics panel
        VBox statsPanel = createStatsPanel();
        mainLayout.setTop(statsPanel);
        
        // Create main content with tabs
        TabPane tabPane = createMainContent();
        mainLayout.setCenter(tabPane);
        
        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setTitle("MediaLab Assistant");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        updateStatistics();
    }

    private VBox createStatsPanel() {
        VBox stats = new VBox(10);
        stats.setPadding(new Insets(10));
        stats.setStyle("-fx-background-color: #f0f0f0;");

        totalTasksLabel = new Label("Total Tasks: 0");
        completedTasksLabel = new Label("Completed Tasks: 0");
        delayedTasksLabel = new Label("Delayed Tasks: 0");
        upcomingTasksLabel = new Label("Upcoming Tasks (7 days): 0");

        stats.getChildren().addAll(
            totalTasksLabel, completedTasksLabel, 
            delayedTasksLabel, upcomingTasksLabel
        );

        return stats;
    }

    private TabPane createMainContent() {
        TabPane tabPane = new TabPane();
        
        // Tasks Tab
        Tab tasksTab = new Tab("Tasks");
        tasksTab.setContent(createTasksView());
        tasksTab.setClosable(false);
        
        // Categories Tab
        Tab categoriesTab = new Tab("Categories");
        categoriesTab.setContent(createCategoriesView());
        categoriesTab.setClosable(false);
        
        // Priorities Tab
        Tab prioritiesTab = new Tab("Priorities");
        prioritiesTab.setContent(createPrioritiesView());
        prioritiesTab.setClosable(false);
        
        // Reminders Tab
        Tab remindersTab = new Tab("Reminders");
        remindersTab.setContent(createRemindersView());
        remindersTab.setClosable(false);
        
        // Search Tab
        Tab searchTab = new Tab("Search");
        searchTab.setContent(createSearchView());
        searchTab.setClosable(false);
        
        tabPane.getTabs().addAll(tasksTab, categoriesTab, prioritiesTab, remindersTab, searchTab);
        return tabPane;
    }

    private VBox createTasksView() {
        VBox tasksView = new VBox(10);
        tasksView.setPadding(new Insets(10));

        // Create task table
        taskTable = new TableView<>();
        
        TableColumn<Task, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Task, Category> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        TableColumn<Task, Priority> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        
        TableColumn<Task, LocalDate> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        
        TableColumn<Task, TaskStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        taskTable.getColumns().addAll(titleCol, categoryCol, priorityCol, deadlineCol, statusCol);
        taskTable.setItems(FXCollections.observableArrayList(taskController.getAllTasks()));

        // Create buttons
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("Add Task");
        Button editButton = new Button("Edit Task");
        Button deleteButton = new Button("Delete Task");
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton);

        // Add event handlers
        addButton.setOnAction(e -> showAddTaskDialog());
        editButton.setOnAction(e -> showEditTaskDialog(taskTable.getSelectionModel().getSelectedItem()));
        deleteButton.setOnAction(e -> deleteSelectedTask());

        tasksView.getChildren().addAll(taskTable, buttonBox);
        return tasksView;
    }

    // Similar pattern for other views...
    private VBox createCategoriesView() {
        VBox categoriesView = new VBox(10);
        categoriesView.setPadding(new Insets(10));
    
        // Create categories table
        categoryTable = new TableView<>();
        
        TableColumn<Category, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Category, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        categoryTable.getColumns().addAll(idCol, nameCol);
        categoryTable.setItems(FXCollections.observableArrayList(categoryController.getAllCategories()));
    
        // Create buttons
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("Add Category");
        Button editButton = new Button("Edit Category");
        Button deleteButton = new Button("Delete Category");
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton);
    
        // Add event handlers
        addButton.setOnAction(e -> showAddCategoryDialog());
        editButton.setOnAction(e -> showEditCategoryDialog(categoryTable.getSelectionModel().getSelectedItem()));
        deleteButton.setOnAction(e -> deleteSelectedCategory());
    
        categoriesView.getChildren().addAll(categoryTable, buttonBox);
        return categoriesView;
    }

    private VBox createPrioritiesView() {
        VBox prioritiesView = new VBox(10);
        prioritiesView.setPadding(new Insets(10));
    
        // Create priorities table
        priorityTable = new TableView<>();
        
        TableColumn<Priority, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Priority, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        priorityTable.getColumns().addAll(idCol, nameCol);
        priorityTable.setItems(FXCollections.observableArrayList(priorityController.getAllPriorities()));
    
        // Create buttons
        HBox buttonBox = new HBox(10);
        Button editButton = new Button("Edit Priority");
        Button deleteButton = new Button("Delete Priority");
        
        buttonBox.getChildren().addAll(editButton, deleteButton);
    
        // Add event handlers
        editButton.setOnAction(e -> showEditPriorityDialog(priorityTable.getSelectionModel().getSelectedItem()));
        deleteButton.setOnAction(e -> deleteSelectedPriority());
    
        prioritiesView.getChildren().addAll(priorityTable, buttonBox);
        return prioritiesView;
    }

    private VBox createRemindersView() {
    VBox remindersView = new VBox(10);
    remindersView.setPadding(new Insets(10));

    // Create reminders table
    reminderTable = new TableView<>();
    
    TableColumn<Reminder, String> idCol = new TableColumn<>("ID");
    idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
    
    TableColumn<Reminder, Task> taskCol = new TableColumn<>("Task");
    taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));
    
    TableColumn<Reminder, ReminderType> typeCol = new TableColumn<>("Type");
    typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    
    TableColumn<Reminder, LocalDate> dateCol = new TableColumn<>("Reminder Date");
    dateCol.setCellValueFactory(new PropertyValueFactory<>("reminderDate"));
    
    reminderTable.getColumns().addAll(idCol, taskCol, typeCol, dateCol);
    reminderTable.setItems(FXCollections.observableArrayList(reminderController.getAllReminders()));

    // Create buttons
    HBox buttonBox = new HBox(10);
    Button addButton = new Button("Add Reminder");
    Button editButton = new Button("Edit Reminder");
    Button deleteButton = new Button("Delete Reminder");
    
    buttonBox.getChildren().addAll(addButton, editButton, deleteButton);

    // Add event handlers
    addButton.setOnAction(e -> showAddReminderDialog());
    editButton.setOnAction(e -> showEditReminderDialog(reminderTable.getSelectionModel().getSelectedItem()));
    deleteButton.setOnAction(e -> deleteSelectedReminder());

    remindersView.getChildren().addAll(reminderTable, buttonBox);
    return remindersView;
    }

    private Node createSearchView() {
        VBox searchView = new VBox(10);
        searchView.setPadding(new Insets(10));
    
        // Title filter
        TextField titleFilter = new TextField();
        titleFilter.setPromptText("Search by title...");
    
        // Category filter
        ComboBox<Category> categoryFilter = new ComboBox<>();
        categoryFilter.setItems(FXCollections.observableArrayList(categoryController.getAllCategories()));
        categoryFilter.setPromptText("Filter by category");
        categoryFilter.setValue(null);
        
        // Set custom cell factory for category display
        categoryFilter.setCellFactory(lv -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText("All Categories");
                } else {
                    setText(category.getName()); // Assuming Category has a getName() method
                }
            }
        });
        
        // Set custom button cell for category display
        categoryFilter.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText("All Categories");
                } else {
                    setText(category.getName());
                }
            }
        });
    
        // Priority filter
        ComboBox<Priority> priorityFilter = new ComboBox<>();
        priorityFilter.setItems(FXCollections.observableArrayList(priorityController.getAllPriorities()));
        priorityFilter.setPromptText("Filter by priority");
        priorityFilter.setValue(null);
        
        // Set custom cell factory for priority display
        priorityFilter.setCellFactory(lv -> new ListCell<Priority>() {
            @Override
            protected void updateItem(Priority priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText("All Priorities");
                } else {
                    setText(priority.getName()); // Assuming Priority has a getName() method
                }
            }
        });
        
        // Set custom button cell for priority display
        priorityFilter.setButtonCell(new ListCell<Priority>() {
            @Override
            protected void updateItem(Priority priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText("All Priorities");
                } else {
                    setText(priority.getName());
                }
            }
        });
    
        // Results table setup
        TableView<Task> searchResults = new TableView<>();
        setupSearchResultsTable(searchResults);
    
        // Search button
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String searchTitle = titleFilter.getText();
            Category selectedCategory = categoryFilter.getValue();
            Priority selectedPriority = priorityFilter.getValue();
            
            List<Task> results = taskController.searchTasks(searchTitle, selectedCategory, selectedPriority);
            searchResults.setItems(FXCollections.observableArrayList(results));
        });
    
        // Clear button
        Button clearButton = new Button("Clear Filters");
        clearButton.setOnAction(e -> {
            titleFilter.clear();
            categoryFilter.setValue(null);
            priorityFilter.setValue(null);
            searchResults.setItems(FXCollections.observableArrayList(taskController.getAllTasks()));
        });
    
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(searchButton, clearButton);
    
        // Layout
        searchView.getChildren().addAll(
            new Label("Search Criteria:"),
            new Label("Title:"), titleFilter,
            new Label("Category:"), categoryFilter,
            new Label("Priority:"), priorityFilter,
            buttonBox,
            searchResults
        );
    
        return searchView;
    }

    private void setupSearchResultsTable(TableView<Task> searchResults) {
        TableColumn<Task, String> titleCol = new TableColumn<>("Title");
        TableColumn<Task, Category> categoryCol = new TableColumn<>("Category");
        TableColumn<Task, Priority> priorityCol = new TableColumn<>("Priority");
        TableColumn<Task, LocalDate> deadlineCol = new TableColumn<>("Deadline");
        TableColumn<Task, TaskStatus> statusCol = new TableColumn<>("Status");
    
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setCellFactory(column -> new TableCell<Task, Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    setText(category.getName());
                }
            }
        });
    
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.setCellFactory(column -> new TableCell<Task, Priority>() {
            @Override
            protected void updateItem(Priority priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                } else {
                    setText(priority.getName());
                }
            }
        });
    
        deadlineCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    
        searchResults.getColumns().addAll(titleCol, categoryCol, priorityCol, deadlineCol, statusCol);
    }
    

    private void updateStatistics() {
        int totalTasks = taskController.getAllTasks().size();
        int completedTasks = taskController.getCompletedTasks().size();
        int delayedTasks = taskController.getDelayedTasks().size();
        int upcomingTasks = taskController.getUpcomingTasks(7).size();

        totalTasksLabel.setText("Total Tasks: " + totalTasks);
        completedTasksLabel.setText("Completed Tasks: " + completedTasks);
        delayedTasksLabel.setText("Delayed Tasks: " + delayedTasks);
        upcomingTasksLabel.setText("Upcoming Tasks (7 days): " + upcomingTasks);
    }

    private void checkDelayedTasks() {
        List<Task> delayedTasks = taskController.getDelayedTasks();
        if (!delayedTasks.isEmpty()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Delayed Tasks");
                alert.setHeaderText("You have " + delayedTasks.size() + " delayed tasks!");
                alert.setContentText("Please check your task list for overdue items.");
                alert.showAndWait();
            });
        }
    }

    @Override
    public void stop() {
        taskController.saveTasks();
        categoryController.saveCategories();
        priorityController.savePriorities();
        reminderController.saveReminders();
    }

    // Dialog methods for adding/editing items
    private void showAddTaskDialog() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Add Task");
        dialog.setHeaderText("Create a new task");
    
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
    
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    
        // Title field
        TextField titleField = new TextField();
        titleField.setPromptText("Task title");
    
        // Description field
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Task description");
        descriptionField.setPrefRowCount(3);
    
        // Category ComboBox
        ComboBox<Category> categoryComboBox = new ComboBox<>();
        categoryComboBox.setItems(FXCollections.observableArrayList(categoryController.getAllCategories()));
        categoryComboBox.setPromptText("Select Category");
    
        // Priority ComboBox
        ComboBox<Priority> priorityComboBox = new ComboBox<>();
        priorityComboBox.setItems(FXCollections.observableArrayList(priorityController.getAllPriorities()));
        priorityComboBox.setPromptText("Select Priority");
    
        // Deadline DatePicker
        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setPromptText("Select Deadline");
    
        // Add fields to grid
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryComboBox, 1, 2);
        grid.add(new Label("Priority:"), 0, 3);
        grid.add(priorityComboBox, 1, 3);
        grid.add(new Label("Deadline:"), 0, 4);
        grid.add(deadlinePicker, 1, 4);
    
        dialog.getDialogPane().setContent(grid);
    
        // Enable/Disable save button depending on whether a title was entered
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
    
        // Validate input
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });
    
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String title = titleField.getText();
                    String description = descriptionField.getText();
                    Category category = categoryComboBox.getValue();
                    Priority priority = priorityComboBox.getValue();
                    LocalDate deadline = deadlinePicker.getValue();
    
                    // Validate required fields
                    if (title.trim().isEmpty()) {
                        throw new IllegalArgumentException("Title is required");
                    }
                    if (category == null) {
                        throw new IllegalArgumentException("Category is required");
                    }
                    if (priority == null) {
                        throw new IllegalArgumentException("Priority is required");
                    }
                    if (deadline == null) {
                        throw new IllegalArgumentException("Deadline is required");
                    }
    
                    // Create and return new task
                    Task task = new Task();
                    task.setTitle(title);
                    task.setDescription(description);
                    task.setCategory(category);
                    task.setPriority(priority);
                    task.setDeadline(deadline);
                    task.setStatus(TaskStatus.OPEN);
                    
                    return task;
                } catch (IllegalArgumentException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Cannot create task");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });
    
        Optional<Task> result = dialog.showAndWait();
        result.ifPresent(task -> {
            try {
                taskController.addTask(task);
                updateTaskTable();
                updateStatistics();
            } catch (IllegalArgumentException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot add task");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void showEditTaskDialog(Task task) {
        if (task == null) return;
    
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Edit task details");
    
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
    
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    
        // Title field
        TextField titleField = new TextField(task.getTitle());
        titleField.setPromptText("Task title");
    
        // Description field
        TextArea descriptionField = new TextArea(task.getDescription());
        descriptionField.setPromptText("Task description");
        descriptionField.setPrefRowCount(3);
    
        // Category ComboBox
        ComboBox<Category> categoryComboBox = new ComboBox<>();
        categoryComboBox.setItems(FXCollections.observableArrayList(categoryController.getAllCategories()));
        categoryComboBox.setValue(task.getCategory());
        categoryComboBox.setPromptText("Select Category");
    
        // Priority ComboBox
        ComboBox<Priority> priorityComboBox = new ComboBox<>();
        priorityComboBox.setItems(FXCollections.observableArrayList(priorityController.getAllPriorities()));
        priorityComboBox.setValue(task.getPriority());
        priorityComboBox.setPromptText("Select Priority");
    
        // Deadline DatePicker
        DatePicker deadlinePicker = new DatePicker(task.getDeadline());
        deadlinePicker.setPromptText("Select Deadline");
    
        // Status ComboBox
        ComboBox<TaskStatus> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(FXCollections.observableArrayList(TaskStatus.values()));
        statusComboBox.setValue(task.getStatus());
        statusComboBox.setPromptText("Select Status");
    
        // Add fields to grid
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryComboBox, 1, 2);
        grid.add(new Label("Priority:"), 0, 3);
        grid.add(priorityComboBox, 1, 3);
        grid.add(new Label("Deadline:"), 0, 4);
        grid.add(deadlinePicker, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusComboBox, 1, 5);
    
        dialog.getDialogPane().setContent(grid);
    
        // Enable/Disable save button depending on whether a title was entered
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
    
        // Validate input
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });
    
        // Set initial save button state
        saveButton.setDisable(false);
    
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String title = titleField.getText();
                    String description = descriptionField.getText();
                    Category category = categoryComboBox.getValue();
                    Priority priority = priorityComboBox.getValue();
                    LocalDate deadline = deadlinePicker.getValue();
                    TaskStatus status = statusComboBox.getValue();
    
                    // Validate required fields
                    if (title.trim().isEmpty()) {
                        throw new IllegalArgumentException("Title is required");
                    }
                    if (category == null) {
                        throw new IllegalArgumentException("Category is required");
                    }
                    if (priority == null) {
                        throw new IllegalArgumentException("Priority is required");
                    }
                    if (deadline == null) {
                        throw new IllegalArgumentException("Deadline is required");
                    }
                    if (status == null) {
                        throw new IllegalArgumentException("Status is required");
                    }
    
                    // Update task with new values
                    task.setTitle(title);
                    task.setDescription(description);
                    task.setCategory(category);
                    task.setPriority(priority);
                    task.setDeadline(deadline);
                    task.setStatus(status);
    
                    return task;
                } catch (IllegalArgumentException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Cannot update task");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });
    
        Optional<Task> result = dialog.showAndWait();
        result.ifPresent(updatedTask -> {
            try {
                taskController.updateTask(updatedTask);
                updateTaskTable();
                updateStatistics();
            } catch (IllegalArgumentException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot update task");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
    }
    

    private void deleteSelectedTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskController.deleteTask(selectedTask);
            updateTaskTable();
            updateStatistics();
        }
    }

    private void updateTaskTable() {
        taskTable.setItems(FXCollections.observableArrayList(taskController.getAllTasks()));
    }

    // Helper methods for Categories
    private void showAddCategoryDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Create a new category");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Category name");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a category name when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return nameField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(categoryName -> {
            categoryController.addCategory(categoryName);
            updateCategoryTable();
        });
    }

    private void showEditCategoryDialog(Category category) {
        if (category == null) return;
    
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Category");
        dialog.setHeaderText("Edit category name");
    
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
    
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    
        TextField nameField = new TextField(category.getName());
    
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
    
        dialog.getDialogPane().setContent(grid);
    
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return nameField.getText();
            }
            return null;
        });
    
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            categoryController.updateCategory(category, newName);
            updateCategoryTable();
        });
    }
    

    private void deleteSelectedCategory() {
        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Category");
            alert.setHeaderText("Delete Category");
            alert.setContentText("Are you sure you want to delete this category? This will also delete all associated tasks.");
    
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                categoryController.deleteCategory(selectedCategory);
                updateCategoryTable();
                updateTaskTable(); // Update task table as tasks might have been deleted
                updateStatistics(); // Update statistics as tasks might have been deleted
            }
        }
    }
    
    // Helper methods for updating tables
    private void updateCategoryTable() {
        categoryTable.setItems(FXCollections.observableArrayList(categoryController.getAllCategories()));
    }
    
    private void updatePriorityTable() {
        priorityTable.setItems(FXCollections.observableArrayList(priorityController.getAllPriorities()));
    }
    
    private void updateReminderTable() {
        reminderTable.setItems(FXCollections.observableArrayList(reminderController.getAllReminders()));
    }

    private void showEditPriorityDialog(Priority priority) {
        if (priority == null) return;
    
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Priority");
        dialog.setHeaderText("Edit priority name");
    
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
    
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    
        TextField nameField = new TextField(priority.getName());
    
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
    
        dialog.getDialogPane().setContent(grid);
    
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return nameField.getText();
            }
            return null;
        });
    
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            try {
                priorityController.updatePriority(priority, newName);
                updatePriorityTable();
                updateTaskTable(); // Update tasks as they might reference this priority
            } catch (IllegalArgumentException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot update priority");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
    }
    
    private void deleteSelectedPriority() {
        Priority selectedPriority = priorityTable.getSelectionModel().getSelectedItem();
        if (selectedPriority != null) {
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Priority");
                alert.setHeaderText("Delete Priority");
                alert.setContentText("Are you sure you want to delete this priority? Tasks with this priority will be reassigned to the default priority.");
    
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    priorityController.deletePriority(selectedPriority);
                    updatePriorityTable();
                    updateTaskTable(); // Update tasks as they might have been reassigned
                }
            } catch (IllegalArgumentException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot delete priority");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    private void showAddPriorityDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Priority");
        dialog.setHeaderText("Create a new priority");
    
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
    
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    
        TextField nameField = new TextField();
        nameField.setPromptText("Priority name");
    
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
    
        dialog.getDialogPane().setContent(grid);
    
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return nameField.getText();
            }
            return null;
        });
    
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            try {
                priorityController.addPriority(name);
                updatePriorityTable();
            } catch (IllegalArgumentException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot add priority");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void showAddReminderDialog() {
        Dialog<Reminder> dialog = new Dialog<>();
        dialog.setTitle("Add Reminder");
        dialog.setHeaderText("Create a new reminder");
    
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
    
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    
        // Task ComboBox
        ComboBox<Task> taskComboBox = new ComboBox<>();
        taskComboBox.setItems(FXCollections.observableArrayList(taskController.getAllTasks()));
        taskComboBox.setPromptText("Select Task");
    
        // Reminder Type ComboBox
        ComboBox<ReminderType> typeComboBox = new ComboBox<>();
        typeComboBox.setItems(FXCollections.observableArrayList(ReminderType.values()));
        typeComboBox.setPromptText("Select Reminder Type");
    
        // Custom Date Picker
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Custom Date");
        datePicker.setDisable(true); // Initially disabled
    
        // Enable/disable date picker based on reminder type selection
        typeComboBox.setOnAction(e -> {
            datePicker.setDisable(typeComboBox.getValue() != ReminderType.CUSTOM_DATE);
        });
    
        grid.add(new Label("Task:"), 0, 0);
        grid.add(taskComboBox, 1, 0);
        grid.add(new Label("Reminder Type:"), 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(new Label("Custom Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
    
        dialog.getDialogPane().setContent(grid);
    
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Task selectedTask = taskComboBox.getValue();
                    ReminderType selectedType = typeComboBox.getValue();
                    LocalDate customDate = datePicker.getValue();
    
                    if (selectedTask == null || selectedType == null) {
                        throw new IllegalArgumentException("Please select both task and reminder type");
                    }
    
                    if (selectedType == ReminderType.CUSTOM_DATE && customDate == null) {
                        throw new IllegalArgumentException("Please select a custom date");
                    }
    
                    return reminderController.createReminder(selectedTask, selectedType, customDate);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Cannot create reminder");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });
    
        Optional<Reminder> result = dialog.showAndWait();
        result.ifPresent(reminder -> {
            updateReminderTable();
        });
    }
    
    private void showEditReminderDialog(Reminder reminder) {
        if (reminder == null) return;
    
        Dialog<Reminder> dialog = new Dialog<>();
        dialog.setTitle("Edit Reminder");
        dialog.setHeaderText("Edit reminder details");
    
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
    
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    
        // Reminder Type ComboBox
        ComboBox<ReminderType> typeComboBox = new ComboBox<>();
        typeComboBox.setItems(FXCollections.observableArrayList(ReminderType.values()));
        typeComboBox.setValue(reminder.getType());
    
        // Custom Date Picker
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(reminder.getReminderDate());
        datePicker.setDisable(reminder.getType() != ReminderType.CUSTOM_DATE);
    
        // Enable/disable date picker based on reminder type selection
        typeComboBox.setOnAction(e -> {
            datePicker.setDisable(typeComboBox.getValue() != ReminderType.CUSTOM_DATE);
        });
    
        grid.add(new Label("Task:"), 0, 0);
        grid.add(new Label(reminder.getTask().getTitle()), 1, 0);
        grid.add(new Label("Reminder Type:"), 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(new Label("Custom Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
    
        dialog.getDialogPane().setContent(grid);
    
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    ReminderType selectedType = typeComboBox.getValue();
                    LocalDate customDate = datePicker.getValue();
    
                    if (selectedType == null) {
                        throw new IllegalArgumentException("Please select a reminder type");
                    }
    
                    if (selectedType == ReminderType.CUSTOM_DATE && customDate == null) {
                        throw new IllegalArgumentException("Please select a custom date");
                    }
    
                    reminderController.updateReminder(reminder, selectedType, customDate);
                    return reminder;
                } catch (IllegalArgumentException | IllegalStateException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Cannot update reminder");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });
    
        Optional<Reminder> result = dialog.showAndWait();
        result.ifPresent(updatedReminder -> {
            updateReminderTable();
        });
    }
    
    private void deleteSelectedReminder() {
        Reminder selectedReminder = reminderTable.getSelectionModel().getSelectedItem();
        if (selectedReminder != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Reminder");
            alert.setHeaderText("Delete Reminder");
            alert.setContentText("Are you sure you want to delete this reminder?");
    
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                reminderController.deleteReminder(selectedReminder);
                updateReminderTable();
            }
        }
    }
    
}
