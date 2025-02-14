package gr.medialab.model;

import java.time.LocalDate;
// import java.util.UUID;
import gr.medialab.model.enums.ReminderType;


public class Reminder {
    private String id;
    private Task task;
    private ReminderType type;
    private LocalDate reminderDate;
    private boolean notified;

    // Constructor
    public Reminder(Task task, ReminderType type, LocalDate reminderDate) {
        this.id = java.util.UUID.randomUUID().toString();
        this.task = task;
        this.type = type;
        this.reminderDate = reminderDate;
        this.notified = false;
    }

    //getters
    public String getId() {return id;}
    public Task getTask() {return task;}
    public ReminderType getType() {return type;}
    public LocalDate getReminderDate() { return reminderDate; }
    public boolean isNotified() { return notified; }

    //setters
    public void setId(String id) { this.id = id; }
    public void setTask(Task task) { this.task = task; }
    public void setReminderDate(LocalDate reminderDAte) { this.reminderDate = reminderDate; }
    public void setType(ReminderType type) {this.type = type;}
    public void setNotified(boolean notified) { this.notified = notified; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return id.equals(reminder.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
