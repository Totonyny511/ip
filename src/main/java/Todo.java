/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description the text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected TaskType getTaskType() {
        return TaskType.TODO;
    }
}
