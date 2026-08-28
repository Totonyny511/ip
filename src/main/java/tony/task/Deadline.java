package tony.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    /** Format used when showing a deadline to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** The date by which this task must be completed. */
    private final LocalDate by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the task
     * @param by the deadline date
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        if (by == null) {
            throw new IllegalArgumentException("A deadline date cannot be null");
        }
        this.by = by;
    }

    /**
     * Returns the deadline task category.
     *
     * @return the deadline task type
     */
    @Override
    protected TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    /**
     * Returns whether the deadline falls on the specified date.
     *
     * @param date the date to check
     * @return whether the date is the deadline date
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        return by.equals(date);
    }

    /**
     * Formats this deadline as one line for storage on disk.
     *
     * @return the task fields followed by the deadline date
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + by;
    }

    /**
     * Formats this deadline for display in the task list.
     *
     * @return the task details followed by the formatted deadline date
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
