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

            if (command.equals("list")) {
                printTasks(tasks, numberOfTasks);
            } else if (command.startsWith("mark ")) {
                markTask(command, tasks, numberOfTasks);
            } else if (command.startsWith("unmark ")) {
                unmarkTask(command, tasks, numberOfTasks);
            } else {
                tasks[numberOfTasks] = new Task(command);
                numberOfTasks++;
                System.out.println("added: " + command);
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
     * Marks the one-based task number in a {@code mark} command as complete.
     * Invalid task numbers leave the stored tasks unchanged.
     *
     * @param command the entered command, beginning with {@code mark }
     * @param tasks the array containing stored tasks
     * @param numberOfTasks how many positions in {@code tasks} contain tasks
     */
    private static void markTask(String command, Task[] tasks, int numberOfTasks) {
        try {
            int taskNumber = Integer.parseInt(command.substring("mark ".length()).trim());
            int taskIndex = taskNumber - 1;

            if (taskIndex < 0 || taskIndex >= numberOfTasks) {
                System.out.println("That task number is not in your list.");
                return;
            }

            tasks[taskIndex].markAsDone();
            System.out.println("Nice! I've marked this task as done:");
            System.out.println("  " + tasks[taskIndex]);
        } catch (NumberFormatException exception) {
            System.out.println("Please provide a task number to mark.");
        }
    }

    /**
     * Marks the one-based task number in an {@code unmark} command as incomplete.
     * Invalid task numbers leave the stored tasks unchanged.
     *
     * @param command the entered command, beginning with {@code unmark }
     * @param tasks the array containing stored tasks
     * @param numberOfTasks how many positions in {@code tasks} contain tasks
     */
    private static void unmarkTask(String command, Task[] tasks, int numberOfTasks) {
        try {
            int taskNumber = Integer.parseInt(command.substring("unmark ".length()).trim());
            int taskIndex = taskNumber - 1;

            if (taskIndex < 0 || taskIndex >= numberOfTasks) {
                System.out.println("That task number is not in your list.");
                return;
            }

            tasks[taskIndex].markAsUndone();
            System.out.println("OK, I've marked this task as not done yet:");
            System.out.println("  " + tasks[taskIndex]);
        } catch (NumberFormatException exception) {
            System.out.println("Please provide a task number to unmark.");
        }
    }
}
