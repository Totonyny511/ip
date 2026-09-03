package tony;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
