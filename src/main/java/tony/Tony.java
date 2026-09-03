package tony;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import tony.exception.TonyException;
import tony.storage.Storage;
import tony.task.Deadline;
import tony.task.Event;
import tony.task.Task;
import tony.task.TaskList;
import tony.task.Todo;
import tony.ui.Ui;

/**
 * Processes commands for the Tony chatbot and stores the user's tasks.
 */
public class Tony {
    /** Default location of the task data file. */
    private static final Path DEFAULT_DATA_FILE = Path.of("./data/tony.txt");

    /** Required format for dates entered in commands. */
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /** Message displayed when Tony cannot read its data file. */
    private static final String LOADING_ERROR_MESSAGE =
            "Warning: I couldn't read the data file. Starting with an empty task list.";

    /** Message displayed when Tony cannot save the current tasks. */
    private static final String SAVING_ERROR_MESSAGE =
            "Warning: I couldn't save your tasks. Your latest changes are only in this session.";

    /** Stores tasks between application sessions. */
    private final Storage storage;

    /** Tasks available during the current session. */
    private final TaskList tasks;

    /** Optional warning generated while loading the saved tasks. */
    private final String startupMessage;

    /** Creates Tony using the default task data file. */
    public Tony() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates Tony using a specified task data file.
     * This overload allows callers such as tests to keep their data isolated.
     *
     * @param dataFile file used to load and save tasks.
     */
    public Tony(Path dataFile) {
        storage = new Storage(dataFile);

        TaskList loadedTasks;
        String loadingMessage = "";
        try {
            Storage.LoadResult result = storage.load();
            loadedTasks = new TaskList(result.getTasks());
            if (result.getSkippedLineCount() > 0) {
                loadingMessage = formatSkippedDataLines(result.getSkippedLineCount());
            }
        } catch (IOException exception) {
            loadedTasks = new TaskList();
            loadingMessage = LOADING_ERROR_MESSAGE;
        }

        tasks = loadedTasks;
        startupMessage = loadingMessage;
    }

    /**
     * Runs Tony using its original console interface.
     *
     * @param args command-line arguments (not used by this application).
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Tony tony = new Tony();
        if (!tony.getStartupMessage().isEmpty()) {
            ui.showMessage(tony.getStartupMessage());
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();
            ui.showMessage(tony.getResponse(command));
            ui.showLine();

            if (isExitCommand(command)) {
                break;
            }
        }
    }

    /**
     * Returns any warning generated while loading saved tasks.
     * An empty string means that startup completed normally.
     *
     * @return the startup warning, or an empty string if there is none.
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Executes one user command and returns Tony's complete reply.
     *
     * @param command complete command entered by the user.
     * @return Tony's reply for the command.
     */
    public String getResponse(String command) {
        if (isExitCommand(command)) {
            return "Bye. Hope to see you again soon!";
        }

        try {
            if (command.equals("list")) {
                return formatTasks("Here are the tasks in your list:", tasks);
            } else if (isCommand(command, "find")) {
                return formatTasks("Here are the matching tasks in your list:", findTasks(command, tasks));
            } else if (isCommand(command, "mark")) {
                Task task = markTask(command, tasks);
                return appendSavingWarning("Nice! I've marked this task as done:\n  " + task);
            } else if (isCommand(command, "unmark")) {
                Task task = unmarkTask(command, tasks);
                return appendSavingWarning("OK, I've marked this task as not done yet:\n  " + task);
            } else if (isCommand(command, "delete")) {
                Task task = deleteTask(command, tasks);
                String response = "Noted. I've removed this task:\n  " + task
                        + "\nNow you have " + formatTaskCount(tasks.size()) + " in the list.";
                return appendSavingWarning(response);
            } else if (isCommand(command, "todo")) {
                return addTask(createTodo(command));
            } else if (isCommand(command, "deadline")) {
                return addTask(createDeadline(command));
            } else if (isCommand(command, "event")) {
                return addTask(createEvent(command));
            }
            throw new TonyException("I don't recognize that command. "
                    + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.");
        } catch (TonyException exception) {
            return "Oops: " + exception.getMessage();
        }
    }

    /**
     * Returns whether a command ends the current conversation.
     *
     * @param command complete command entered by the user.
     * @return whether the command is {@code bye}.
     */
    public static boolean isExitCommand(String command) {
        return command.equals("bye");
    }

    /** Stores a task and returns a confirmation with the updated task count. */
    private String addTask(Task task) {
        tasks.add(task);
        String response = "Got it. I've added this task:\n  " + task
                + "\nNow you have " + formatTaskCount(tasks.size()) + " in the list.";
        return appendSavingWarning(response);
    }

    /** Saves the current tasks and appends a warning to the reply after a disk error. */
    private String appendSavingWarning(String response) {
        try {
            storage.save(tasks);
            return response;
        } catch (IOException exception) {
            return response + "\n" + SAVING_ERROR_MESSAGE;
        }
    }

    /** Marks the one-based task number in a {@code mark} command as complete. */
    private static Task markTask(String command, TaskList tasks) throws TonyException {
        int taskIndex = getTaskIndex(command, "mark", tasks.size());
        return tasks.mark(taskIndex);
    }

    /** Marks the one-based task number in an {@code unmark} command as incomplete. */
    private static Task unmarkTask(String command, TaskList tasks) throws TonyException {
        int taskIndex = getTaskIndex(command, "unmark", tasks.size());
        return tasks.unmark(taskIndex);
    }

    /** Removes the one-based task number supplied in a {@code delete} command. */
    private static Task deleteTask(String command, TaskList tasks) throws TonyException {
        int taskIndex = getTaskIndex(command, "delete", tasks.size());
        return tasks.delete(taskIndex);
    }

    /** Finds tasks whose descriptions contain the keyword in a {@code find} command. */
    private static TaskList findTasks(String command, TaskList tasks) throws TonyException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new TonyException("Please provide a keyword to find.");
        }
        return tasks.find(keyword);
    }

    /** Returns whether the input is a command word, optionally followed by arguments. */
    private static boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /** Creates a to-do after checking that it has a description. */
    private static Todo createTodo(String command) throws TonyException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new TonyException("A to-do needs a description. For example: todo read chapter 3");
        }
        return new Todo(description);
    }

    /** Creates a deadline after checking its description and {@code /by} value. */
    private static Deadline createDeadline(String command) throws TonyException {
        String details = command.substring("deadline".length()).trim();
        int byMarker = details.indexOf(" /by ");
        if (byMarker <= 0 || byMarker + " /by ".length() >= details.length()) {
            throw new TonyException("A deadline needs a description and a due date. "
                    + "Use: deadline <task> /by <yyyy-MM-dd>");
        }
        LocalDate dueDate = parseDate(details.substring(byMarker + " /by ".length()).trim());
        return new Deadline(details.substring(0, byMarker).trim(), dueDate);
    }

    /** Creates an event after checking its description, start, and end values. */
    private static Event createEvent(String command) throws TonyException {
        String details = command.substring("event".length()).trim();
        int fromMarker = details.indexOf(" /from ");
        int toMarker = fromMarker < 0
                ? -1
                : details.indexOf(" /to ", fromMarker + " /from ".length());
        if (fromMarker <= 0 || toMarker <= fromMarker + " /from ".length()
                || toMarker + " /to ".length() >= details.length()) {
            throw new TonyException("An event needs a description, start date, and end date. "
                    + "Use: event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd>");
        }
        LocalDate startDate = parseDate(
                details.substring(fromMarker + " /from ".length(), toMarker).trim());
        LocalDate endDate = parseDate(details.substring(toMarker + " /to ".length()).trim());
        if (endDate.isBefore(startDate)) {
            throw new TonyException("An event's end date cannot be before its start date.");
        }
        return new Event(details.substring(0, fromMarker).trim(), startDate, endDate);
    }

    /** Parses a date entered in the required ISO format. */
    private static LocalDate parseDate(String dateText) throws TonyException {
        try {
            return LocalDate.parse(dateText, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new TonyException("Please enter dates as yyyy-MM-dd (for example, 2019-10-15).");
        }
    }

    /** Parses and checks the task number supplied to a list-changing command. */
    private static int getTaskIndex(String command, String commandWord, int numberOfTasks)
            throws TonyException {
        try {
            String numberText = command.substring(commandWord.length()).trim();
            if (numberText.isEmpty()) {
                throw new TonyException("Please provide a task number to " + commandWord + ".");
            }
            int taskNumber = Integer.parseInt(numberText);
            int taskIndex = taskNumber - 1;

            if (taskIndex < 0 || taskIndex >= numberOfTasks) {
                throw new TonyException("That task number is not in your list.");
            }
            return taskIndex;
        } catch (NumberFormatException exception) {
            throw new TonyException("Please provide a whole-number task number to " + commandWord + ".");
        }
    }

    /** Formats a heading and tasks as a numbered, multi-line response. */
    private static String formatTasks(String heading, TaskList tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int index = 0; index < tasks.size(); index++) {
            response.append('\n')
                    .append(index + 1)
                    .append('.')
                    .append(tasks.get(index));
        }
        return response.toString();
    }

    /** Formats a task count with the appropriate singular or plural noun. */
    private static String formatTaskCount(int taskCount) {
        return taskCount + (taskCount == 1 ? " task" : " tasks");
    }

    /** Formats a warning for invalid data lines skipped during startup. */
    private static String formatSkippedDataLines(int lineCount) {
        String formattedCount = lineCount + (lineCount == 1 ? " line" : " lines");
        return "Warning: I skipped " + formattedCount
                + " in the data file because they were invalid.";
    }
}
