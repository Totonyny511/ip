/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    /** The deadline text entered by the user. */
    private final String by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the task
     * @param by the deadline text
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    protected TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + escapeDataField(by);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
