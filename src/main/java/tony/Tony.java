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
 * Starts the Tony chatbot application.
 */
public class Tony {
    /** Default location of the task data file. */
    private static final Path DEFAULT_DATA_FILE = Path.of("./data/tony.txt");

    /** Required format for dates entered in commands. */
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Displays Tony's greeting, stores entered tasks, lists them on request,
     * and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments (not used by this application)
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Storage storage = new Storage(DEFAULT_DATA_FILE);
        TaskList tasks = loadTasks(storage, ui);

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();

            if (command.equals("bye")) {
                ui.showExit();
                ui.showLine();
                break;
            }

            try {
                if (command.equals("list")) {
                    ui.showTasks(tasks);
                } else if (isCommand(command, "mark")) {
                    ui.showTaskMarked(markTask(command, tasks));
                    saveTasks(storage, tasks, ui);
                } else if (isCommand(command, "unmark")) {
                    ui.showTaskUnmarked(unmarkTask(command, tasks));
                    saveTasks(storage, tasks, ui);
                } else if (isCommand(command, "delete")) {
                    ui.showTaskDeleted(deleteTask(command, tasks), tasks.size());
                    saveTasks(storage, tasks, ui);
                } else if (isCommand(command, "todo")) {
                    addTask(tasks, createTodo(command), ui);
                    saveTasks(storage, tasks, ui);
                } else if (isCommand(command, "deadline")) {
                    addTask(tasks, createDeadline(command), ui);
                    saveTasks(storage, tasks, ui);
                } else if (isCommand(command, "event")) {
                    addTask(tasks, createEvent(command), ui);
                    saveTasks(storage, tasks, ui);
                } else {
                    throw new TonyException("I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.");
                }
            } catch (TonyException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showLine();
        }
    }

    /**
     * Loads saved tasks while allowing the chatbot to start if the file cannot be read.
     *
     * @param storage the task storage to read
     * @param ui the console UI used to show loading warnings
     * @return the valid tasks loaded from disk, or an empty list after a read error
     */
    private static TaskList loadTasks(Storage storage, Ui ui) {
        try {
            Storage.LoadResult result = storage.load();
            if (result.getSkippedLineCount() > 0) {
                ui.showSkippedDataLines(result.getSkippedLineCount());
            }
            return new TaskList(result.getTasks());
        } catch (IOException exception) {
            ui.showLoadingError();
            return new TaskList();
        }
    }

    /**
     * Saves tasks while keeping the current session usable after a disk error.
     *
     * @param storage the task storage to write
     * @param tasks the current tasks
     * @param ui the console UI used to show saving warnings
     */
    private static void saveTasks(Storage storage, TaskList tasks, Ui ui) {
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            ui.showSavingError();
        }
    }

    /**
     * Stores a task and shows the updated task count.
     *
     * @param tasks the list in which to store the task
     * @param task the task to store
     * @param ui the console UI used to show confirmation
     */
    private static void addTask(TaskList tasks, Task task, Ui ui) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Marks the one-based task number in a {@code mark} command as complete.
     * Invalid task numbers leave the stored tasks unchanged.
     *
     * @param command the entered command, beginning with {@code mark }
     * @param tasks the tasks currently stored in the list
     * @return the marked task
     * @throws TonyException if the task number is missing, invalid, or out of range
     */
    private static Task markTask(String command, TaskList tasks) throws TonyException {
        int taskIndex = getTaskIndex(command, "mark", tasks.size());
        return tasks.mark(taskIndex);
    }

    /**
     * Marks the one-based task number in an {@code unmark} command as incomplete.
     *
     * @param command the entered command, beginning with {@code unmark}
     * @param tasks the tasks currently stored in the list
     * @return the unmarked task
     * @throws TonyException if the task number is missing, invalid, or out of range
     */
    private static Task unmarkTask(String command, TaskList tasks) throws TonyException {
        int taskIndex = getTaskIndex(command, "unmark", tasks.size());
        return tasks.unmark(taskIndex);
    }

    /**
     * Removes the one-based task number supplied in a {@code delete} command.
     *
     * @param command the entered command, beginning with {@code delete}
     * @param tasks the tasks currently stored in the list
     * @return the deleted task
     * @throws TonyException if the task number is missing, invalid, or out of range
     */
    private static Task deleteTask(String command, TaskList tasks) throws TonyException {
        int taskIndex = getTaskIndex(command, "delete", tasks.size());
        return tasks.delete(taskIndex);
    }

    /**
     * Returns whether the input is a command word, optionally followed by arguments.
     *
     * @param input the complete user input
     * @param commandWord the command word to match
     * @return whether the input names the specified command
     */
    private static boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /**
     * Creates a to-do after checking that it has a description.
     *
     * @param command the complete to-do command
     * @return the validated to-do task
     * @throws TonyException if the description is empty
     */
    private static Todo createTodo(String command) throws TonyException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new TonyException("A to-do needs a description. For example: todo read chapter 3");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline after checking its description and {@code /by} value.
     *
     * @param command the complete deadline command
     * @return the validated deadline task
     * @throws TonyException if the command is missing required fields
     */
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

    /**
     * Creates an event after checking its description, start, and end values.
     *
     * @param command the complete event command
     * @return the validated event task
     * @throws TonyException if the command is missing required fields
     */
    private static Event createEvent(String command) throws TonyException {
        String details = command.substring("event".length()).trim();
        int fromMarker = details.indexOf(" /from ");
        int toMarker = fromMarker < 0 ? -1 : details.indexOf(" /to ", fromMarker + " /from ".length());
        if (fromMarker <= 0 || toMarker <= fromMarker + " /from ".length()
                || toMarker + " /to ".length() >= details.length()) {
            throw new TonyException("An event needs a description, start date, and end date. "
                    + "Use: event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd>");
        }
        LocalDate startDate = parseDate(details.substring(fromMarker + " /from ".length(), toMarker).trim());
        LocalDate endDate = parseDate(details.substring(toMarker + " /to ".length()).trim());
        if (endDate.isBefore(startDate)) {
            throw new TonyException("An event's end date cannot be before its start date.");
        }
        return new Event(details.substring(0, fromMarker).trim(), startDate, endDate);
    }

    /**
     * Parses a date entered in the required ISO format.
     *
     * @param dateText the user-entered date
     * @return the parsed date
     * @throws TonyException if the text is not a valid {@code yyyy-MM-dd} date
     */
    private static LocalDate parseDate(String dateText) throws TonyException {
        try {
            return LocalDate.parse(dateText, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new TonyException("Please enter dates as yyyy-MM-dd (for example, 2019-10-15).");
        }
    }

    /**
     * Parses and checks the task number supplied to a mark or unmark command.
     *
     * @param command the complete command
     * @param commandWord either {@code mark} or {@code unmark}
     * @param numberOfTasks how many tasks are currently stored
     * @return the zero-based index of the requested task
     * @throws TonyException if the task number is missing, invalid, or out of range
     */
    private static int getTaskIndex(String command, String commandWord, int numberOfTasks) throws TonyException {
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
}
