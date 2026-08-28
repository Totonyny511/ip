package tony.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task with a start date and an end date.
 */
public class Event extends Task {
    /** Format used when showing event dates to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** The first date of the event. */
    private final LocalDate from;

    /** The last date of the event. */
    private final LocalDate to;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the event
     * @param from the event start date
     * @param to the event end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Event dates cannot be null");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("An event's end date cannot be before its start date");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event task category.
     *
     * @return the event task type
     */
    @Override
    protected TaskType getTaskType() {
        return TaskType.EVENT;
    }

    /**
     * Returns whether the specified date is within the event's inclusive date range.
     *
     * @param date the date to check
     * @return whether the date is between the start and end dates, inclusive
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        return date != null && !date.isBefore(from) && !date.isAfter(to);
    }

    /**
     * Formats this event as one line for storage on disk.
     *
     * @return the task fields followed by the event's start and end dates
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + from + " | " + to;
    }

    /**
     * Formats this event for display in the task list.
     *
     * @return the task details followed by the formatted event date range
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
