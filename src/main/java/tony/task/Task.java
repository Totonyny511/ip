package tony.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents a task that can be completed or left incomplete.
 */
public abstract class Task {
    /** The text describing what must be done. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to show this task's completion status.
     *
     * @return {@code X} when complete, or a space when incomplete.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns whether this task's description contains a keyword, ignoring letter case.
     *
     * @param keyword text to search for in the description
     * @return whether the keyword occurs in the description
     */
    public boolean descriptionContains(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns this task's category.
     *
     * @return the task's type.
     */
    protected abstract TaskType getTaskType();

    /**
     * Returns whether this task falls on the specified date.
     * Tasks without a date return {@code false}.
     *
     * @param date the date to check.
     * @return whether this task falls on the date.
     */
    public boolean isOnDate(LocalDate date) {
        return false;
    }

    /**
     * Formats this task as one line for storage on disk.
     *
     * @return the task type, completion status, and description.
     */
    public String toDataString() {
        return getTaskType().getIcon() + " | " + (isDone ? "1" : "0") + " | "
                + escapeDataField(description);
    }

    /**
     * Escapes characters that have a special meaning in the storage format.
     *
     * @param value a task field to store.
     * @return the field with backslashes and pipe characters escaped.
     */
    protected String escapeDataField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Formats this task for display in the task list.
     *
     * @return the type icon, status icon, and description of this task.
     */
    @Override
    public String toString() {
        return "[" + getTaskType().getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
