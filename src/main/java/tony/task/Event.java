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
    private final LocalDate startDate;

    /** The last date of the event. */
    private final LocalDate endDate;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the event.
     * @param startDate the event start date.
     * @param endDate the event end date.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Event dates cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("An event's end date cannot be before its start date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected TaskType getTaskType() {
        return TaskType.EVENT;
    }

    @Override
    public boolean isOnDate(LocalDate date) {
        return date != null && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + startDate + " | " + endDate;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + startDate.format(DISPLAY_DATE_FORMAT)
                + " to: " + endDate.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
