package gr.medialab.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//import gr.medialab.controller.ReminderController;
import gr.medialab.model.enums.TaskStatus;



public class Task {
    private String id;
    private String title;
    private String description;
    private Category category;
    private Priority priority;
    private LocalDate deadline;
    private TaskStatus status;
    // private ReminderController reminderController;
    private List<Reminder> reminders;

    // // Add default constructor
    // public Task() {
    //     // Initialize with default values if needed
    //     this.id = "";
    //     this.title = "";
    //     this.description = "";
    //     this.status = TaskStatus.OPEN;  // Default status
    //     this.deadline = LocalDate.now(); // Default to today
    //     this.priority = new Priority("LOW"); // Default priority
    //     this.category = new Category("GENERAL"); // Default category
    //     this.reminders = new ArrayList<>();//empty
    // }
    // Default constructor
    public Task() {
        this.id = UUID.randomUUID().toString();
        this.status = TaskStatus.OPEN;  // Default status
        this.reminders = new ArrayList<>();
    }
    
    // public void setReminderController(ReminderController reminderController) {
    //     this.reminderController = reminderController;
    // }
    
    // //Constructor
    // public Task(String title, String description, Category category, Priority priority, LocalDate deadline) {
    //     this.id = UUID.randomUUID().toString(); 
    //     this.title = title;
    //     this.description = description;
    //     this.category = category;
    //     this.priority = priority;
    //     this.deadline = deadline;
    //     this.status = TaskStatus.OPEN;
    //     this.reminders = new ArrayList<>();
    // }
    // Full constructor
    public Task(String title, String description, Category category, 
                Priority priority, LocalDate deadline) {
        this();  // Call default constructor for id and default status
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.deadline = deadline;
    }

    //getters
    public String getId() {return id;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public Category getCategory() {return category;}
    public Priority getPriority() {return priority;}
    public LocalDate getDeadline() {return deadline;}
    public TaskStatus getStatus() {return status;}
    public List<Reminder> getReminders(){return reminders;}

    //setters
    public void setId(String id) {this.id = id;}
    public void setTitle(String title) {this.title = title;}
    public void setDescription(String description) {this.description = description;}
    public void setCategory(Category category) {this.category = category;}
    public void setPriority(Priority priority) {this.priority = priority;}
    public void setDeadline(LocalDate deadline) {this.deadline = deadline;}
    public void setStatus(TaskStatus status) {this.status = status;}
    
    //Reminder functions
    public void addReminder(Reminder reminder){
        if (!status.equals(TaskStatus.COMPLETED)){
            reminders.add(reminder);
        }
    }

    public void removeReminder(Reminder reminder){
        reminders.remove(reminder);
    }


    //other usefull functions
    // Add method to check if task is overdue
    public boolean isOverdue() {
        return !status.equals(TaskStatus.COMPLETED) && 
               deadline.isBefore(LocalDate.now());
    }

    // Add method to update status to DELAYED if overdue
    public void updateStatusIfOverdue() {
        if (isOverdue()) {
            this.status = TaskStatus.DELAYED;
        }
    }

    //helps for display
    @Override
    public String toString() {
        return getTitle();
    }
    
    public boolean isCompleted() {
        return status.equals(TaskStatus.COMPLETED);
    }

    // public void setCompleted(boolean completed) {
    //     if (completed) {
    //         this.status = TaskStatus.COMPLETED;
    //         if (reminderController != null) {
    //             reminderController.deleteTaskReminders(this);
    //         }
    //         reminders.clear();
    //     } else {
    //         this.status = TaskStatus.OPEN;
    //     }
    // }
}

