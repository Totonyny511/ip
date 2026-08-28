package tony.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the application's tasks and provides operations for changing the list.
 */
public class TaskList {
    /** The tasks currently managed by the application. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     * The tasks are copied so callers cannot later change this list indirectly.
     *
     * @param tasks tasks with which to initialize the list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param taskIndex zero-based index of the task to remove
     * @return the removed task
     */
    public Task delete(int taskIndex) {
        return tasks.remove(taskIndex);
    }

    /**
     * Marks the task at a zero-based index as complete.
     *
     * @param taskIndex zero-based index of the task to mark
     * @return the task after it has been marked
     */
    public Task mark(int taskIndex) {
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at a zero-based index as incomplete.
     *
     * @param taskIndex zero-based index of the task to unmark
     * @return the task after it has been unmarked
     */
    public Task unmark(int taskIndex) {
        Task task = tasks.get(taskIndex);
        task.markAsUndone();
        return task;
    }

    /**
     * Finds tasks whose descriptions contain the supplied keyword, ignoring letter case.
     * The returned list is independent of this list, but contains the same task objects.
     *
     * @param keyword text to search for in task descriptions
     * @return matching tasks in their original list order
     */
    public TaskList find(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.descriptionContains(keyword)) {
                matchingTasks.add(task);
            }
        }
        return new TaskList(matchingTasks);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param taskIndex zero-based index of the task
     * @return the requested task
     */
    public Task get(int taskIndex) {
        return tasks.get(taskIndex);
    }

    /**
     * Returns how many tasks are in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an immutable snapshot for saving the current tasks.
     *
     * @return a snapshot of the tasks in list order
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
