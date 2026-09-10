package tony;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command processing through the entry point shared by the console and GUI.
 */
public class TonyTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies the UI contract that command processing receives a non-null line. */
    @Test
    public void getResponse_nullCommand_throwsAssertionError() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        AssertionError error = assertThrows(AssertionError.class, () -> tony.getResponse(null));

        assertEquals("A command read from the UI must not be null", error.getMessage());
    }

    /** Verifies that core task commands update and display the same task list. */
    @Test
    public void getResponse_taskWorkflow_returnsCurrentTaskState() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 task in the list.", tony.getResponse("todo read book"));
        assertEquals("Got it. I've added this task:\n"
                + "  [D][ ] submit report (by: Sep 20 2026)\n"
                + "Now you have 2 tasks in the list.",
                tony.getResponse("deadline submit report /by 2026-09-20"));
        assertEquals("Nice! I've marked this task as done:\n  [T][X] read book",
                tony.getResponse("mark 1"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][X] read book\n"
                + "2.[D][ ] submit report (by: Sep 20 2026)", tony.getResponse("list"));
        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[D][ ] submit report (by: Sep 20 2026)", tony.getResponse("find REPORT"));
        assertEquals("Noted. I've removed this task:\n"
                + "  [T][X] read book\n"
                + "Now you have 1 task in the list.", tony.getResponse("delete 1"));
    }

    /** Verifies that one command deletes several original-list positions and reports them in list order. */
    @Test
    public void getResponse_deleteMultipleTasks_deletesOriginalPositionsInListOrder() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        tony.getResponse("todo read book");
        tony.getResponse("deadline submit report /by 2026-09-20");
        tony.getResponse("event orientation /from 2026-09-21 /to 2026-09-22");
        tony.getResponse("todo buy groceries");

        assertEquals("Noted. I've removed these tasks:\n"
                + "  [D][ ] submit report (by: Sep 20 2026)\n"
                + "  [T][ ] buy groceries\n"
                + "Now you have 2 tasks in the list.", tony.getResponse("delete 4 2"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[E][ ] orientation (from: Sep 21 2026 to: Sep 22 2026)",
                tony.getResponse("list"));
    }

    /** Verifies that any invalid selection prevents every deletion in a multi-delete command. */
    @Test
    public void getResponse_invalidMultiDelete_returnsErrorWithoutDeletingTasks() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        tony.getResponse("todo first task");
        tony.getResponse("todo second task");
        tony.getResponse("todo third task");

        assertEquals("Oops: Please provide a whole-number task number to delete.",
                tony.getResponse("delete 1 two"));
        assertEquals("Oops: That task number is not in your list.", tony.getResponse("delete 1 4"));
        assertEquals("Oops: Please provide each task number only once.", tony.getResponse("delete 1 1"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] first task\n"
                + "2.[T][ ] second task\n"
                + "3.[T][ ] third task", tony.getResponse("list"));
    }

    /** Verifies that the result of a multi-delete is available in a later session. */
    @Test
    public void constructor_tasksDeletedTogether_restoresRemainingTasks() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Tony firstSession = new Tony(dataFile);
        firstSession.getResponse("todo first task");
        firstSession.getResponse("todo second task");
        firstSession.getResponse("todo third task");
        firstSession.getResponse("delete 3 1");

        Tony secondSession = new Tony(dataFile);

        assertEquals("Here are the tasks in your list:\n1.[T][ ] second task",
                secondSession.getResponse("list"));
    }

    /** Verifies that invalid commands explain the problem without changing the task list. */
    @Test
    public void getResponse_invalidCommands_returnsErrorsWithoutChangingTasks() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("Oops: A to-do needs a description. For example: todo read chapter 3",
                tony.getResponse("todo"));
        assertEquals("Oops: Please enter dates as yyyy-MM-dd (for example, 2019-10-15).",
                tony.getResponse("deadline submit report /by tomorrow"));
        assertEquals("Oops: I don't recognize that command. "
                + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.",
                tony.getResponse("hello"));
        assertEquals("Here are the tasks in your list:", tony.getResponse("list"));
    }

    /** Verifies that a new chatbot instance loads tasks saved by an earlier instance. */
    @Test
    public void constructor_savedTasksExist_restoresTasks() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Tony firstSession = new Tony(dataFile);
        firstSession.getResponse("todo keep this task");

        Tony secondSession = new Tony(dataFile);

        assertEquals("", secondSession.getStartupMessage());
        assertEquals("Here are the tasks in your list:\n1.[T][ ] keep this task",
                secondSession.getResponse("list"));
    }

    /** Verifies that malformed saved records produce a warning while valid records remain usable. */
    @Test
    public void constructor_malformedSavedRecord_warnsAndKeepsValidTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of("T | 0 | valid task", "T | 2 | invalid status"));

        Tony tony = new Tony(dataFile);

        assertEquals("Warning: I skipped 1 line in the data file because they were invalid.",
                tony.getStartupMessage());
        assertEquals("Here are the tasks in your list:\n1.[T][ ] valid task", tony.getResponse("list"));
    }

    /** Verifies that the exit command returns Tony's farewell. */
    @Test
    public void getResponse_bye_returnsFarewell() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("Bye. Hope to see you again soon!", tony.getResponse("bye"));
    }
}
