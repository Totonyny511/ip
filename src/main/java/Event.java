/**
 * Represents a task with a start time and an end time.
 */
public class Event extends Task {
    /** The event start text entered by the user. */
    private final String from;

    /** The event end text entered by the user. */
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the event
     * @param from the event start text
     * @param to the event end text
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected TaskType getTaskType() {
        return TaskType.EVENT;
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + escapeDataField(from) + " | " + escapeDataField(to);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
