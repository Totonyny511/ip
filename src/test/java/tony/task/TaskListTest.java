package tony.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list mutation and protection of its internal list structure.
 */
public class TaskListTest {
    /** Verifies that construction protects the task list from later source-list changes. */
    @Test
    public void constructor_sourceListChangedAfterConstruction_taskListUnaffected() {
        Todo originalTask = new Todo("original task");
        ArrayList<Task> sourceTasks = new ArrayList<>(List.of(originalTask));
        TaskList taskList = new TaskList(sourceTasks);

        sourceTasks.clear();

        assertEquals(1, taskList.size());
        assertSame(originalTask, taskList.get(0));
    }

    /** Verifies that adding a task appends it to the list. */
    @Test
    public void add_task_appendsTask() {
        Todo firstTask = new Todo("first task");
        Todo secondTask = new Todo("second task");
        TaskList taskList = new TaskList(List.of(firstTask));

        taskList.add(secondTask);

        assertEquals(2, taskList.size());
        assertSame(secondTask, taskList.get(1));
    }

    /** Verifies that deletion removes and returns the selected task. */
    @Test
    public void delete_middleTask_removesAndReturnsTask() {
        Todo firstTask = new Todo("first task");
        Todo middleTask = new Todo("middle task");
        Todo lastTask = new Todo("last task");
        TaskList taskList = new TaskList(List.of(firstTask, middleTask, lastTask));

        Task deletedTask = taskList.delete(1);

        assertSame(middleTask, deletedTask);
        assertEquals(List.of(firstTask, lastTask), taskList.getTasks());
    }

    /** Verifies that marking a task completes and returns the selected task. */
    @Test
    public void mark_validIndex_marksAndReturnsSelectedTask() {
        Todo firstTask = new Todo("first task");
        Todo selectedTask = new Todo("selected task");
        TaskList taskList = new TaskList(List.of(firstTask, selectedTask));

        Task markedTask = taskList.mark(1);

        assertSame(selectedTask, markedTask);
        assertEquals("X", selectedTask.getStatusIcon());
        assertEquals(" ", firstTask.getStatusIcon());
    }

    /** Verifies that unmarking a task makes and returns it as incomplete. */
    @Test
    public void unmark_completedTask_marksAndReturnsSelectedTaskAsIncomplete() {
        Todo selectedTask = new Todo("selected task");
        selectedTask.markAsDone();
        TaskList taskList = new TaskList(List.of(selectedTask));

        Task unmarkedTask = taskList.unmark(0);

        assertSame(selectedTask, unmarkedTask);
        assertEquals(" ", selectedTask.getStatusIcon());
    }

    /** Verifies that callers cannot modify the returned task snapshot. */
    @Test
    public void getTasks_returnedListCannotBeModified() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));
        List<Task> tasks = taskList.getTasks();

        assertThrows(UnsupportedOperationException.class,
                () -> tasks.add(new Todo("another task")));
    }

    /** Verifies that a task snapshot is unaffected by later additions. */
    @Test
    public void getTasks_taskAddedAfterSnapshot_snapshotUnaffected() {
        TaskList taskList = new TaskList(List.of(new Todo("original task")));
        List<Task> snapshot = taskList.getTasks();

        taskList.add(new Todo("later task"));

        assertEquals(1, snapshot.size());
        assertEquals(2, taskList.size());
    }

    /** Verifies that list operations reject an out-of-range index. */
    @Test
    public void mutationMethods_indexOutOfRange_throwIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(0));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(0));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(0));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));
    }
}
