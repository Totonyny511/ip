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

    @Override
    protected TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    @Override
    public boolean isOnDate(LocalDate date) {
        return by.equals(date);
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + by;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
