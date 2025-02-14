package gr.medialab.controller;

import gr.medialab.model.Reminder;
import gr.medialab.model.Task;
import gr.medialab.model.enums.ReminderType;
import gr.medialab.model.enums.TaskStatus;
import gr.medialab.service.JsonReminderManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for managing reminders in the application.
 * Handles creation, modification, deletion and validation of reminders.
 */
public class ReminderController {
    private final List<Reminder> reminders;
    private final JsonReminderManager jsonManager;
    private static final String REMINDERS_FILE = "reminders.json";

    /**
     * Constructor initializes the controller
     * @param taskController The task controller instance
     */
    public ReminderController(TaskController taskController) {
        this.jsonManager = new JsonReminderManager(taskController);
        this.reminders = loadReminders();
    }

    public List<Reminder> loadReminders() {
        List<Reminder> loadedReminders = jsonManager.loadReminders(REMINDERS_FILE);
        return loadedReminders != null ? loadedReminders : new ArrayList<>();
    }

    public void saveReminders() {
        jsonManager.saveReminders(reminders, REMINDERS_FILE);
    }

    /**
     * Validates if a reminder can be added to a task
     * @param task The task to check
     * @throws IllegalStateException if task is completed
     */
    private void validateTaskForReminder(Task task) {
        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new IllegalStateException("Cannot add reminders to completed tasks");
        }
    }

    /**
     * Validates if a reminder date is valid for a task
     * @param task The task
     * @param reminderDate The proposed reminder date
     * @throws IllegalArgumentException if reminder date is invalid
     */
    private void validateReminderDate(Task task, LocalDate reminderDate) {
        LocalDate today = LocalDate.now();
        LocalDate deadline = task.getDeadline();

        if (reminderDate.isAfter(deadline)) {
            throw new IllegalArgumentException("Reminder date cannot be after task deadline");
        }
        if (reminderDate.isBefore(today)) {
            throw new IllegalArgumentException("Reminder date cannot be in the past");
        }
    }

    /**
     * Validates if a reminder type is appropriate for a task's deadline
     * @param task The task
     * @param type The reminder type
     * @throws IllegalArgumentException if reminder type is invalid for the task
     */
    private void validateReminderType(Task task, ReminderType type) {
        LocalDate deadline = task.getDeadline();
        LocalDate today = LocalDate.now();
        long daysUntilDeadline = ChronoUnit.DAYS.between(today, deadline);
    
        switch (type) {
            case ONE_DAY_BEFORE:
                if (daysUntilDeadline <= 1) {
                    throw new IllegalArgumentException("Task deadline is too soon for a one-day reminder");
                }
                break;
            case ONE_WEEK_BEFORE:
                if (daysUntilDeadline <= 7) {
                    throw new IllegalArgumentException("Task deadline is too soon for a one-week reminder");
                }
                break;
            case ONE_MONTH_BEFORE:
                if (daysUntilDeadline <= 30) {
                    throw new IllegalArgumentException("Task deadline is too soon for a one-month reminder");
                }
                break;
            case CUSTOM_DATE:
                // No validation needed for custom date as it's handled separately
                break;
            default:
                throw new IllegalArgumentException("Unknown reminder type");
        }
    }
    

    /**
     * Creates a new reminder for a task
     * @param task The task
     * @param type The reminder type
     * @param customDate Custom date for CUSTOM_DATE type reminders
     * @return The created reminder
     */
    public Reminder createReminder(Task task, ReminderType type, LocalDate customDate) {
        validateTaskForReminder(task);

        LocalDate reminderDate;
        if (type == ReminderType.CUSTOM_DATE) {
            if (customDate == null) {
                throw new IllegalArgumentException("Custom date is required for custom reminder type");
            }
            reminderDate = customDate;
        } else {
            validateReminderType(task, type);
            reminderDate = calculateReminderDate(task.getDeadline(), type);
        }

        validateReminderDate(task, reminderDate);

        Reminder reminder = new Reminder(task, type, reminderDate);
        reminders.add(reminder);
        saveReminders();
        return reminder;
    }

    /**
     * Calculates the reminder date based on reminder type and task deadline
     */
    private LocalDate calculateReminderDate(LocalDate deadline, ReminderType type) {
        switch (type) {
            case ONE_DAY_BEFORE:
                return deadline.minusDays(1);
            case ONE_WEEK_BEFORE:
                return deadline.minusWeeks(1);
            case ONE_MONTH_BEFORE:
                return deadline.minusMonths(1);
            case CUSTOM_DATE:
                throw new IllegalArgumentException("Cannot calculate date for custom reminder type");
            default:
                throw new IllegalArgumentException("Unknown reminder type");
        }
    }

    /**
     * Updates an existing reminder
     */
    public void updateReminder(Reminder reminder, ReminderType newType, LocalDate newCustomDate) {
        validateTaskForReminder(reminder.getTask());
        
        LocalDate newReminderDate;
        if (newType == ReminderType.CUSTOM_DATE) {
            if (newCustomDate == null) {
                throw new IllegalArgumentException("Custom date is required for custom reminder type");
            }
            newReminderDate = newCustomDate;
        } else {
            validateReminderType(reminder.getTask(), newType);
            newReminderDate = calculateReminderDate(reminder.getTask().getDeadline(), newType);
        }

        validateReminderDate(reminder.getTask(), newReminderDate);
        
        reminder.setType(newType);
        reminder.setReminderDate(newReminderDate);
        saveReminders();
    }

    /**
     * Deletes a reminder
     */
    public void deleteReminder(Reminder reminder) {
        reminders.remove(reminder);
        saveReminders();
    }

    /**
     * Deletes all reminders for a task
     */
    public void deleteTaskReminders(Task task) {
        reminders.removeIf(reminder -> reminder.getTask().getId().equals(task.getId()));
        saveReminders();
    }

    /**
     * Gets all reminders for a task
     */
    public List<Reminder> getRemindersForTask(Task task) {
        List<Reminder> taskReminders = new ArrayList<>();
        for (Reminder reminder : reminders) {
            if (reminder.getTask().getId().equals(task.getId())) {
                taskReminders.add(reminder);
            }
        }
        return taskReminders;
    }

    /**
     * Gets all reminders
     */
    public List<Reminder> getAllReminders() {
        return new ArrayList<>(reminders);
    }
}
