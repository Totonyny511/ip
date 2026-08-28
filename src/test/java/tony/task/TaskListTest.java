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
    @Test
    public void constructor_sourceListChangedAfterConstruction_taskListUnaffected() {
        Todo originalTask = new Todo("original task");
        ArrayList<Task> sourceTasks = new ArrayList<>(List.of(originalTask));
        TaskList taskList = new TaskList(sourceTasks);

        sourceTasks.clear();

        assertEquals(1, taskList.size());
        assertSame(originalTask, taskList.get(0));
    }

    @Test
    public void add_task_appendsTask() {
        Todo firstTask = new Todo("first task");
        Todo secondTask = new Todo("second task");
        TaskList taskList = new TaskList(List.of(firstTask));

        taskList.add(secondTask);

        assertEquals(2, taskList.size());
        assertSame(secondTask, taskList.get(1));
    }

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

    @Test
    public void unmark_completedTask_marksAndReturnsSelectedTaskAsIncomplete() {
        Todo selectedTask = new Todo("selected task");
        selectedTask.markAsDone();
        TaskList taskList = new TaskList(List.of(selectedTask));

        Task unmarkedTask = taskList.unmark(0);

        assertSame(selectedTask, unmarkedTask);
        assertEquals(" ", selectedTask.getStatusIcon());
    }

    @Test
    public void find_keywordWithDifferentCase_returnsMatchingDescriptionsInOriginalOrder() {
        Todo firstMatch = new Todo("Read Book");
        Todo nonMatch = new Todo("write report");
        Deadline secondMatch = new Deadline("return book", java.time.LocalDate.of(2019, 6, 6));
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        TaskList matches = taskList.find("BOOK");

        assertEquals(2, matches.size());
        assertSame(firstMatch, matches.get(0));
        assertSame(secondMatch, matches.get(1));
    }

    @Test
    public void find_keywordOnlyInFormattedDate_returnsNoMatches() {
        Deadline task = new Deadline("return library item", java.time.LocalDate.of(2019, 6, 6));
        TaskList taskList = new TaskList(List.of(task));

        TaskList matches = taskList.find("Jun");

        assertEquals(0, matches.size());
    }

    @Test
    public void getTasks_returnedListCannotBeModified() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));
        List<Task> tasks = taskList.getTasks();

        assertThrows(UnsupportedOperationException.class,
                () -> tasks.add(new Todo("another task")));
    }

    @Test
    public void getTasks_taskAddedAfterSnapshot_snapshotUnaffected() {
        TaskList taskList = new TaskList(List.of(new Todo("original task")));
        List<Task> snapshot = taskList.getTasks();

        taskList.add(new Todo("later task"));

        assertEquals(1, snapshot.size());
        assertEquals(2, taskList.size());
    }

    @Test
    public void mutationMethods_indexOutOfRange_throwIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(0));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(0));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(0));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));
    }
}
