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

    @Override
    protected TaskType getTaskType() {
        return TaskType.EVENT;
    }

    @Override
    public boolean isOnDate(LocalDate date) {
        return date != null && !date.isBefore(from) && !date.isAfter(to);
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + from + " | " + to;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
