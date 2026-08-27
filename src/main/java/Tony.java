import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Starts the Tony chatbot application.
 */
public class Tony {
    /** Default location of the task data file. */
    private static final Path DEFAULT_DATA_FILE = Path.of("./data/tony.txt");

    /**
     * Displays Tony's greeting, stores entered tasks, lists them on request,
     * and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments (not used by this application)
     */
    public static void main(String[] args) {
        String banner = " _____   ___   _   _ __   __\n"
                + "|_   _| / _ \\ | \\ | |\\ \\ / /\n"
                + "  | |  | | | ||  \\| | \\ V /\n"
                + "  | |  | |_| || |\\  |  | |\n"
                + "  |_|   \\___/ |_| \\_|  |_|";
        System.out.println(banner);
        String line = "________________________________________________";

        System.out.println(line);
        System.out.println("What can I do for you?");
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage(DEFAULT_DATA_FILE);
        ArrayList<Task> tasks = loadTasks(storage);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(line);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }

            try {
                if (command.equals("list")) {
                    printTasks(tasks);
                } else if (isCommand(command, "mark")) {
                    markTask(command, tasks);
                    saveTasks(storage, tasks);
                } else if (isCommand(command, "unmark")) {
                    unmarkTask(command, tasks);
                    saveTasks(storage, tasks);
                } else if (isCommand(command, "delete")) {
                    deleteTask(command, tasks);
                    saveTasks(storage, tasks);
                } else if (isCommand(command, "todo")) {
                    addTask(tasks, createTodo(command));
                    saveTasks(storage, tasks);
                } else if (isCommand(command, "deadline")) {
                    addTask(tasks, createDeadline(command));
                    saveTasks(storage, tasks);
                } else if (isCommand(command, "event")) {
                    addTask(tasks, createEvent(command));
                    saveTasks(storage, tasks);
                } else {
                    throw new TonyException("I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.");
                }
            } catch (TonyException exception) {
                System.out.println("Oops: " + exception.getMessage());
            }
            System.out.println(line);
        }
    }

    /**
     * Loads saved tasks while allowing the chatbot to start if the file cannot be read.
     *
     * @param storage the task storage to read
     * @return the valid tasks loaded from disk, or an empty list after a read error
     */
    private static ArrayList<Task> loadTasks(Storage storage) {
        try {
            Storage.LoadResult result = storage.load();
            if (result.getSkippedLineCount() > 0) {
                System.out.println("Warning: I skipped " + formatLineCount(result.getSkippedLineCount())
                        + " in the data file because they were invalid.");
            }
            return result.getTasks();
        } catch (IOException exception) {
            System.out.println("Warning: I couldn't read the data file. Starting with an empty task list.");
            return new ArrayList<>();
        }
    }

    /**
     * Saves tasks while keeping the current session usable after a disk error.
     *
     * @param storage the task storage to write
     * @param tasks the current tasks
     */
    private static void saveTasks(Storage storage, List<Task> tasks) {
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            System.out.println("Warning: I couldn't save your tasks. Your latest changes are only in this session.");
        }
    }

    /**
     * Formats the number of invalid data-file lines for a warning message.
     *
     * @param lineCount number of invalid lines
     * @return the count with a singular or plural noun
     */
    private static String formatLineCount(int lineCount) {
        return lineCount + (lineCount == 1 ? " line" : " lines");
    }

    /**
     * Prints each stored task with its completion status and one-based number.
     *
     * @param tasks the tasks currently stored in the list
     */
    private static void printTasks(ArrayList<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println((index + 1) + "." + tasks.get(index));
        }
    }

    /**
     * Stores a task and displays the confirmation message.
     *
     * @param tasks the list in which to store the task
     * @param task the task to store
     */
    private static void addTask(List<Task> tasks, Task task) {
        tasks.add(task);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + formatTaskCount(tasks.size()) + " in the list.");
    }

    /**
     * Marks the one-based task number in a {@code mark} command as complete.
     * Invalid task numbers leave the stored tasks unchanged.
     *
     * @param command the entered command, beginning with {@code mark }
     * @param tasks the tasks currently stored in the list
     * @throws TonyException if the task number is missing, invalid, or out of range
     */
    private static void markTask(String command, List<Task> tasks) throws TonyException {
        int taskIndex = getTaskIndex(command, "mark", tasks.size());
        tasks.get(taskIndex).markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + tasks.get(taskIndex));
    }

    /**
     * Marks the one-based task number in an {@code unmark} command as incomplete.
     *
     * @param command the entered command, beginning with {@code unmark}
     * @param tasks the tasks currently stored in the list
     * @throws TonyException if the task number is missing, invalid, or out of range
     */
    private static void unmarkTask(String command, List<Task> tasks) throws TonyException {
        int taskIndex = getTaskIndex(command, "unmark", tasks.size());
        tasks.get(taskIndex).markAsUndone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + tasks.get(taskIndex));
    }

    /**
     * Removes the one-based task number supplied in a {@code delete} command.
     *
     * @param command the entered command, beginning with {@code delete}
     * @param tasks the tasks currently stored in the list
     * @throws TonyException if the task number is missing, invalid, or out of range
     */
    private static void deleteTask(String command, List<Task> tasks) throws TonyException {
        int taskIndex = getTaskIndex(command, "delete", tasks.size());
        Task removedTask = tasks.remove(taskIndex);
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removedTask);
        System.out.println("Now you have " + formatTaskCount(tasks.size()) + " in the list.");
    }

    /**
     * Formats a task count with the appropriate singular or plural noun.
     *
     * @param taskCount the number of tasks in the list
     * @return the count followed by {@code task} or {@code tasks}
     */
    private static String formatTaskCount(int taskCount) {
        return taskCount + (taskCount == 1 ? " task" : " tasks");
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
            throw new TonyException("A deadline needs a description and a due time. Use: deadline <task> /by <time>");
        }
        return new Deadline(details.substring(0, byMarker).trim(),
                details.substring(byMarker + " /by ".length()).trim());
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
            throw new TonyException("An event needs a description, start, and end. Use: event <task> /from <start> /to <end>");
        }
        return new Event(details.substring(0, fromMarker).trim(),
                details.substring(fromMarker + " /from ".length(), toMarker).trim(),
                details.substring(toMarker + " /to ".length()).trim());
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
