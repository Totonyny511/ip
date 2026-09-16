package tony;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    /** Visual meaning of a response returned to a graphical interface. */
    public enum ResponseType {
        NORMAL,
        WARNING,
        ERROR
    }

    /**
     * Contains a response message and the visual meaning an interface should give it.
     *
     * @param message response text to display.
     * @param type visual meaning of the response.
     */
    public record CommandResult(String message, ResponseType type) {
    }

    /** Default location of the task data file. */
    private static final Path DEFAULT_DATA_FILE = Path.of("./data/tony.txt");

    /** Required format for dates entered in commands. */
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /** Message displayed when Tony cannot read its data file. */
    private static final String LOADING_ERROR_MESSAGE =
            "Chief, I couldn't read our records, so I have opened a fresh agenda for this session.";

    /** Message displayed when Tony cannot save the current tasks. */
    private static final String SAVING_ERROR_MESSAGE =
            "Chief, I couldn't file that change. It will remain available only for this session.";

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
     * Returns the number of tasks currently managed by Tony.
     *
     * @return the total number of tasks.
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Returns the number of tasks currently marked as complete.
     *
     * @return the number of completed tasks.
     */
    public int getCompletedTaskCount() {
        return tasks.countCompletedTasks();
    }

    /**
     * Returns the secretary's short assessment of the chief's current workload.
     *
     * @return a workload-sensitive sentence for the task overview.
     */
    public String getOverviewMessage() {
        int totalTaskCount = tasks.size();
        int incompleteTaskCount = totalTaskCount - tasks.countCompletedTasks();
        if (totalTaskCount == 0) {
            return "Your desk is clear, Chief. I am ready when you are.";
        } else if (incompleteTaskCount == 0) {
            return "Everything is in order, Chief. Shall we call it a day and have a drink?";
        } else if (incompleteTaskCount >= 5) {
            return "The agenda is rather full, Chief. Please remember to take a proper rest.";
        } else if (incompleteTaskCount == 1) {
            return "One matter awaits your attention, Chief. I will keep it on our radar.";
        }
        return incompleteTaskCount + " matters await your attention, Chief. I will keep them in order.";
    }

    /**
     * Executes one user command and returns Tony's complete reply.
     *
     * @param command complete command entered by the user.
     * @return Tony's reply for the command.
     */
    public String getResponse(String command) {
        return getCommandResult(command).message();
    }

    /**
     * Executes one user command and returns its text together with its visual meaning.
     *
     * @param command complete command entered by the user.
     * @return the command result for a graphical interface.
     */
    public CommandResult getCommandResult(String command) {
        assert command != null : "A command read from the UI must not be null";

        if (isExitCommand(command)) {
            return new CommandResult(
                    "The office is in order, Chief. Enjoy your evening.", ResponseType.NORMAL);
        }

        try {
            String response = executeCommand(command);
            ResponseType responseType = response.endsWith(SAVING_ERROR_MESSAGE)
                    ? ResponseType.WARNING
                    : ResponseType.NORMAL;
            return new CommandResult(response, responseType);
        } catch (TonyException exception) {
            return new CommandResult("My apologies, Chief. " + exception.getMessage(), ResponseType.ERROR);
        }
    }

    /** Executes a recognized non-exit command and formats its successful response. */
    private String executeCommand(String command) throws TonyException {
        if (command.equals("list")) {
            return formatTasks("Here is the current agenda, Chief:",
                    "Your agenda is clear, Chief. There are no matters on file.", tasks);
        } else if (isCommand(command, "find")) {
            return formatTasks("I found these matching matters, Chief:",
                    "I found no matching matters, Chief.", findTasks(command, tasks));
        } else if (isCommand(command, "mark")) {
            Task task = markTask(command, tasks);
            return appendSavingWarning(
                    "Excellent, Chief. I've recorded this matter as complete:\n  " + task);
        } else if (isCommand(command, "unmark")) {
            Task task = unmarkTask(command, tasks);
            return appendSavingWarning(
                    "Understood, Chief. I've returned this matter to the active agenda:\n  " + task);
        } else if (isCommand(command, "delete")) {
            List<Task> deletedTasks = deleteTasks(command, tasks);
            String response = formatDeletedTasks(deletedTasks, tasks.size());
            return appendSavingWarning(response);
        } else if (isCommand(command, "todo")) {
            return addTask(createTodo(command),
                    "Certainly, Chief. I've added this item to the agenda:");
        } else if (isCommand(command, "deadline")) {
            return addTask(createDeadline(command),
                    "Consider it scheduled, Chief. I'll keep watch over this deadline:");
        } else if (isCommand(command, "event")) {
            return addTask(createEvent(command),
                    "Your calendar is updated, Chief. I've arranged this event:");
        }
        throw new TonyException("I don't recognize that instruction. "
                + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.");
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
    private String addTask(Task task, String confirmation) {
        tasks.add(task);
        String response = confirmation + "\n  " + task
                + "\nThe agenda now contains " + formatTaskCount(tasks.size()) + ".";
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

    /** Removes the one-based task numbers supplied in a {@code delete} command. */
    private static List<Task> deleteTasks(String command, TaskList tasks) throws TonyException {
        List<Integer> taskIndexes = getTaskIndexes(command, "delete", tasks.size());
        return tasks.deleteTasks(taskIndexes);
    }

    /** Finds tasks whose descriptions contain the keyword in a {@code find} command. */
    private static TaskList findTasks(String command, TaskList tasks) throws TonyException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new TonyException("Please give me a keyword to search for.");
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
            throw new TonyException(
                    "I need a description for the to-do. For example: todo read chapter 3");
        }
        return new Todo(description);
    }

    /** Creates a deadline after checking its description and {@code /by} value. */
    private static Deadline createDeadline(String command) throws TonyException {
        String details = command.substring("deadline".length()).trim();
        int byMarker = details.indexOf(" /by ");
        if (byMarker <= 0 || byMarker + " /by ".length() >= details.length()) {
            throw new TonyException("I need a description and due date for the deadline. "
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
            throw new TonyException("I need a description, start date, and end date for the event. "
                    + "Use: event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd>");
        }
        LocalDate startDate = parseDate(
                details.substring(fromMarker + " /from ".length(), toMarker).trim());
        LocalDate endDate = parseDate(details.substring(toMarker + " /to ".length()).trim());
        if (endDate.isBefore(startDate)) {
            throw new TonyException("I cannot schedule an event to end before it begins.");
        }
        return new Event(details.substring(0, fromMarker).trim(), startDate, endDate);
    }

    /** Parses a date entered in the required ISO format. */
    private static LocalDate parseDate(String dateText) throws TonyException {
        try {
            return LocalDate.parse(dateText, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new TonyException("Please give me dates as yyyy-MM-dd, for example 2019-10-15.");
        }
    }

    /** Parses and checks the task number supplied to a list-changing command. */
    private static int getTaskIndex(String command, String commandWord, int numberOfTasks)
            throws TonyException {
        assert isCommand(command, commandWord) : "The command must match the operation being parsed";
        assert numberOfTasks >= 0 : "A task list cannot have a negative size";

        try {
            String numberText = command.substring(commandWord.length()).trim();
            if (numberText.isEmpty()) {
                throw new TonyException("Please give me a task number to " + commandWord + ".");
            }
            int taskNumber = Integer.parseInt(numberText);
            int taskIndex = taskNumber - 1;

            if (taskIndex < 0 || taskIndex >= numberOfTasks) {
                throw new TonyException("That task number is not on the agenda.");
            }
            assert taskIndex >= 0 && taskIndex < numberOfTasks
                    : "A validated task index must refer to an existing task";
            return taskIndex;
        } catch (NumberFormatException exception) {
            throw new TonyException("Please give me a whole-number task number to " + commandWord + ".");
        }
    }

    /** Parses and checks the task numbers supplied to a multi-item command. */
    private static List<Integer> getTaskIndexes(String command, String commandWord, int numberOfTasks)
            throws TonyException {
        assert isCommand(command, commandWord) : "The command must match the operation being parsed";
        assert numberOfTasks >= 0 : "A task list cannot have a negative size";

        String numberText = command.substring(commandWord.length()).trim();
        if (numberText.isEmpty()) {
            throw new TonyException("Please give me a task number to " + commandWord + ".");
        }

        ArrayList<Integer> taskIndexes = new ArrayList<>();
        Set<Integer> uniqueTaskIndexes = new HashSet<>();
        for (String numberToken : numberText.split("\\s+")) {
            int taskIndex;
            try {
                taskIndex = Integer.parseInt(numberToken) - 1;
            } catch (NumberFormatException exception) {
                throw new TonyException("Please give me a whole-number task number to " + commandWord + ".");
            }

            if (taskIndex < 0 || taskIndex >= numberOfTasks) {
                throw new TonyException("That task number is not on the agenda.");
            }
            if (!uniqueTaskIndexes.add(taskIndex)) {
                throw new TonyException("Please give me each task number only once.");
            }
            taskIndexes.add(taskIndex);
        }
        return taskIndexes;
    }

    /** Formats the confirmation for one or more deleted tasks. */
    private static String formatDeletedTasks(List<Task> deletedTasks, int remainingTaskCount) {
        assert deletedTasks != null : "A deletion response must contain deleted tasks";
        assert !deletedTasks.isEmpty() : "A deletion response requires at least one deleted task";

        if (deletedTasks.size() == 1) {
            return "As requested, Chief. I've removed this matter:\n  " + deletedTasks.get(0)
                    + "\nThe agenda now contains " + formatTaskCount(remainingTaskCount) + ".";
        }

        StringBuilder response = new StringBuilder(
                "As requested, Chief. I've removed these matters:");
        for (Task deletedTask : deletedTasks) {
            response.append("\n  ").append(deletedTask);
        }
        return response.append("\nThe agenda now contains ")
                .append(formatTaskCount(remainingTaskCount))
                .append('.')
                .toString();
    }

    /** Formats a heading and tasks as a numbered, multi-line response. */
    private static String formatTasks(String heading, String emptyMessage, TaskList tasks) {
        if (tasks.size() == 0) {
            return emptyMessage;
        }
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
        assert taskCount >= 0 : "A task count cannot be negative";
        return taskCount + (taskCount == 1 ? " task" : " tasks");
    }

    /** Formats a warning for invalid data lines skipped during startup. */
    private static String formatSkippedDataLines(int lineCount) {
        assert lineCount > 0 : "A skipped-lines warning requires at least one skipped line";
        String formattedCount = lineCount + (lineCount == 1 ? " line" : " lines");
        return "Chief, I set aside " + formattedCount
                + " from our records because the data was invalid.";
    }
}
