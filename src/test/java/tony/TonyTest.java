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

        assertEquals("Certainly, Chief. I've added this item to the agenda:\n"
                + "  [T][ ] read book\n"
                + "The agenda now contains 1 task.", tony.getResponse("todo read book"));
        assertEquals("Consider it scheduled, Chief. I'll keep watch over this deadline:\n"
                + "  [D][ ] submit report (by: Sep 20 2026)\n"
                + "The agenda now contains 2 tasks.",
                tony.getResponse("deadline submit report /by 2026-09-20"));
        assertEquals("Excellent, Chief. I've recorded this matter as complete:\n  [T][X] read book",
                tony.getResponse("mark 1"));
        assertEquals("Here is the current agenda, Chief:\n"
                + "1.[T][X] read book\n"
                + "2.[D][ ] submit report (by: Sep 20 2026)", tony.getResponse("list"));
        assertEquals("I found these matching matters, Chief:\n"
                + "1.[D][ ] submit report (by: Sep 20 2026)", tony.getResponse("find REPORT"));
        assertEquals("As requested, Chief. I've removed this matter:\n"
                + "  [T][X] read book\n"
                + "The agenda now contains 1 task.", tony.getResponse("delete 1"));
    }

    /** Verifies that one command deletes several original-list positions and reports them in list order. */
    @Test
    public void getResponse_deleteMultipleTasks_deletesOriginalPositionsInListOrder() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        tony.getResponse("todo read book");
        tony.getResponse("deadline submit report /by 2026-09-20");
        tony.getResponse("event orientation /from 2026-09-21 /to 2026-09-22");
        tony.getResponse("todo buy groceries");

        assertEquals("As requested, Chief. I've removed these matters:\n"
                + "  [D][ ] submit report (by: Sep 20 2026)\n"
                + "  [T][ ] buy groceries\n"
                + "The agenda now contains 2 tasks.", tony.getResponse("delete 4 2"));
        assertEquals("Here is the current agenda, Chief:\n"
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

        assertEquals("My apologies, Chief. Please give me a whole-number task number to delete.",
                tony.getResponse("delete 1 two"));
        assertEquals("My apologies, Chief. That task number is not on the agenda.",
                tony.getResponse("delete 1 4"));
        assertEquals("My apologies, Chief. Please give me each task number only once.",
                tony.getResponse("delete 1 1"));
        assertEquals("Here is the current agenda, Chief:\n"
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

        assertEquals("Here is the current agenda, Chief:\n1.[T][ ] second task",
                secondSession.getResponse("list"));
    }

    /** Verifies that invalid commands explain the problem without changing the task list. */
    @Test
    public void getResponse_invalidCommands_returnsErrorsWithoutChangingTasks() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("My apologies, Chief. I need a description for the to-do. "
                        + "For example: todo read chapter 3",
                tony.getResponse("todo"));
        assertEquals("My apologies, Chief. Please give me dates as yyyy-MM-dd, for example 2019-10-15.",
                tony.getResponse("deadline submit report /by tomorrow"));
        assertEquals("My apologies, Chief. I don't recognize that instruction. "
                + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.",
                tony.getResponse("hello"));
        assertEquals("Your agenda is clear, Chief. There are no matters on file.",
                tony.getResponse("list"));
    }

    /** Verifies that graphical interfaces receive error meaning separately from error wording. */
    @Test
    public void getCommandResult_invalidCommand_returnsTypedSecretaryError() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        Tony.CommandResult result = tony.getCommandResult("todo");

        assertEquals(Tony.ResponseType.ERROR, result.type());
        assertEquals("My apologies, Chief. I need a description for the to-do. "
                + "For example: todo read chapter 3", result.message());
    }

    /** Verifies that graphical interfaces receive normal response meaning for successful commands. */
    @Test
    public void getCommandResult_validCommand_returnsNormalResponse() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        Tony.CommandResult result = tony.getCommandResult("todo read book");

        assertEquals(Tony.ResponseType.NORMAL, result.type());
        assertEquals("Certainly, Chief. I've added this item to the agenda:\n"
                + "  [T][ ] read book\n"
                + "The agenda now contains 1 task.", result.message());
    }

    /** Verifies that task-summary counts follow task additions, completion, and deletion. */
    @Test
    public void taskCounts_taskWorkflow_returnsCurrentTotals() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals(0, tony.getTaskCount());
        assertEquals(0, tony.getCompletedTaskCount());

        tony.getResponse("todo first task");
        tony.getResponse("todo second task");
        tony.getResponse("mark 2");

        assertEquals(2, tony.getTaskCount());
        assertEquals(1, tony.getCompletedTaskCount());

        tony.getResponse("delete 2");
        assertEquals(1, tony.getTaskCount());
        assertEquals(0, tony.getCompletedTaskCount());
    }

    /** Verifies that the compact overview note responds to each meaningful workload state. */
    @Test
    public void getOverviewMessage_differentWorkloads_returnsSecretaryAdvice() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));
        assertEquals("Your desk is clear, Chief. I am ready when you are.", tony.getOverviewMessage());

        tony.getResponse("todo first task");
        assertEquals("One matter awaits your attention, Chief. I will keep it on our radar.",
                tony.getOverviewMessage());

        for (int taskNumber = 2; taskNumber <= 5; taskNumber++) {
            tony.getResponse("todo task " + taskNumber);
        }
        assertEquals("The agenda is rather full, Chief. Please remember to take a proper rest.",
                tony.getOverviewMessage());

        for (int taskNumber = 1; taskNumber <= 5; taskNumber++) {
            tony.getResponse("mark " + taskNumber);
        }
        assertEquals("Everything is in order, Chief. Shall we call it a day and have a drink?",
                tony.getOverviewMessage());
    }

    /** Verifies that a new chatbot instance loads tasks saved by an earlier instance. */
    @Test
    public void constructor_savedTasksExist_restoresTasks() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Tony firstSession = new Tony(dataFile);
        firstSession.getResponse("todo keep this task");

        Tony secondSession = new Tony(dataFile);

        assertEquals("", secondSession.getStartupMessage());
        assertEquals("Here is the current agenda, Chief:\n1.[T][ ] keep this task",
                secondSession.getResponse("list"));
    }

    /** Verifies that malformed saved records produce a warning while valid records remain usable. */
    @Test
    public void constructor_malformedSavedRecord_warnsAndKeepsValidTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of("T | 0 | valid task", "T | 2 | invalid status"));

        Tony tony = new Tony(dataFile);

        assertEquals("Chief, I set aside 1 line from our records because the data was invalid.",
                tony.getStartupMessage());
        assertEquals("Here is the current agenda, Chief:\n1.[T][ ] valid task", tony.getResponse("list"));
    }

    /** Verifies that the exit command returns Tony's farewell. */
    @Test
    public void getResponse_bye_returnsFarewell() {
        Tony tony = new Tony(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("The office is in order, Chief. Enjoy your evening.", tony.getResponse("bye"));
    }
}
