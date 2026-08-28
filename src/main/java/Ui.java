import java.util.Scanner;

/**
 * Handles all console input and output for the Tony chatbot.
 */
public class Ui {
    /** Tony's name banner displayed at startup. */
    private static final String BANNER = " _____   ___   _   _ __   __\n"
            + "|_   _| / _ \\ | \\ | |\\ \\ / /\n"
            + "  | |  | | | ||  \\| | \\ V /\n"
            + "  | |  | |_| || |\\  |  | |\n"
            + "  |_|   \\___/ |_| \\_|  |_|";

    /** Divider used to separate commands and responses. */
    private static final String LINE = "________________________________________________";

    /** Reads commands entered through standard input. */
    private final Scanner scanner;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Tony's startup banner and greeting. */
    public void showWelcome() {
        System.out.println(BANNER);
        showLine();
        System.out.println("What can I do for you?");
        showLine();
    }

    /** Displays the divider line. */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Returns whether another complete command can be read.
     *
     * @return whether standard input contains another line
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the complete command line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays Tony's farewell message. */
    public void showExit() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Displays all tasks with one-based list numbers.
     *
     * @param tasks tasks to display
     */
    public void showTasks(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println((index + 1) + "." + tasks.get(index));
        }
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task the added task
     * @param taskCount the updated number of tasks
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + formatTaskCount(taskCount) + " in the list.");
    }

    /**
     * Displays confirmation that a task was marked complete.
     *
     * @param task the marked task
     */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Displays confirmation that a task was marked incomplete.
     *
     * @param task the unmarked task
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task the deleted task
     * @param taskCount the updated number of tasks
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + formatTaskCount(taskCount) + " in the list.");
    }

    /**
     * Displays an error caused by invalid user input.
     *
     * @param message explanation of the error
     */
    public void showError(String message) {
        System.out.println("Oops: " + message);
    }

    /**
     * Warns that invalid lines were skipped while loading tasks.
     *
     * @param lineCount number of invalid lines
     */
    public void showSkippedDataLines(int lineCount) {
        String formattedCount = lineCount + (lineCount == 1 ? " line" : " lines");
        System.out.println("Warning: I skipped " + formattedCount
                + " in the data file because they were invalid.");
    }

    /** Warns that the data file could not be read. */
    public void showLoadingError() {
        System.out.println("Warning: I couldn't read the data file. Starting with an empty task list.");
    }

    /** Warns that current task changes could not be saved. */
    public void showSavingError() {
        System.out.println("Warning: I couldn't save your tasks. Your latest changes are only in this session.");
    }

    /**
     * Formats a task count with the appropriate singular or plural noun.
     *
     * @param taskCount the number of tasks
     * @return the count followed by {@code task} or {@code tasks}
     */
    private String formatTaskCount(int taskCount) {
        return taskCount + (taskCount == 1 ? " task" : " tasks");
    }
}
