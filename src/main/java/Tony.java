import java.util.Scanner;

/**
 * Starts the Tony chatbot application.
 */
public class Tony {
    /** Maximum number of tasks that can be kept while the program is running. */
    private static final int MAX_TASKS = 100;

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
        Task[] tasks = new Task[MAX_TASKS];
        int numberOfTasks = 0;

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
                    printTasks(tasks, numberOfTasks);
                } else if (isCommand(command, "mark")) {
                    markTask(command, tasks, numberOfTasks);
                } else if (isCommand(command, "unmark")) {
                    unmarkTask(command, tasks, numberOfTasks);
                } else if (isCommand(command, "todo")) {
                    addTask(tasks, numberOfTasks, createTodo(command));
                    numberOfTasks++;
                } else if (isCommand(command, "deadline")) {
                    addTask(tasks, numberOfTasks, createDeadline(command));
                    numberOfTasks++;
                } else if (isCommand(command, "event")) {
                    addTask(tasks, numberOfTasks, createEvent(command));
                    numberOfTasks++;
                } else {
                    throw new TonyException("I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or bye.");
                }
            } catch (TonyException exception) {
                System.out.println("Oops: " + exception.getMessage());
            }
            System.out.println(line);
        }
    }

    /**
     * Prints each stored task with its completion status and one-based number.
     *
     * @param tasks the array containing stored tasks
     * @param numberOfTasks how many positions in {@code tasks} contain tasks
     */
    private static void printTasks(Task[] tasks, int numberOfTasks) {
        System.out.println("Here are the tasks in your list:");
        for (int index = 0; index < numberOfTasks; index++) {
            System.out.println((index + 1) + "." + tasks[index]);
        }
    }

    /**
     * Stores a task and displays the confirmation message.
     *
     * @param tasks the array containing stored tasks
     * @param numberOfTasks the index at which to store the task
     * @param task the task to store
     * @throws TonyException if the task list has reached its capacity
     */
    private static void addTask(Task[] tasks, int numberOfTasks, Task task) throws TonyException {
        if (numberOfTasks >= MAX_TASKS) {
            throw new TonyException("Your task list is full. Remove a task before adding another one.");
        }
        tasks[numberOfTasks] = task;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + (numberOfTasks + 1) + " tasks in the list.");
    }

    /**
     * Marks the one-based task number in a {@code mark} command as complete.
     * Invalid task numbers leave the stored tasks unchanged.
     *
     * @param command the entered command, beginning with {@code mark }
     * @param tasks the array containing stored tasks
     * @param numberOfTasks how many positions in {@code tasks} contain tasks
     * @throws TonyException if the task number is missing, invalid, or out of range
     */
    private static void markTask(String command, Task[] tasks, int numberOfTasks) throws TonyException {
        int taskIndex = getTaskIndex(command, "mark", numberOfTasks);
        tasks[taskIndex].markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + tasks[taskIndex]);
    }

    /**
     * Marks the one-based task number in an {@code unmark} command as incomplete.
     *
     * @param command the entered command, beginning with {@code unmark}
     * @param tasks the array containing stored tasks
     * @param numberOfTasks how many positions in {@code tasks} contain tasks
     * @throws TonyException if the task number is missing, invalid, or out of range
     */
    private static void unmarkTask(String command, Task[] tasks, int numberOfTasks) throws TonyException {
        int taskIndex = getTaskIndex(command, "unmark", numberOfTasks);
        tasks[taskIndex].markAsUndone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + tasks[taskIndex]);
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
