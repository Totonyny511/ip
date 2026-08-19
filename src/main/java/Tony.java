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
        String[] tasks = new String[MAX_TASKS];
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
            } else {
                tasks[numberOfTasks] = command;
                numberOfTasks++;
                System.out.println("added: " + command);
            }
            System.out.println(line);
        }
    }

    /**
     * Prints each task that has been stored, with numbering that starts at one.
     *
     * @param tasks the array containing stored tasks
     * @param numberOfTasks how many positions in {@code tasks} contain tasks
     */
    private static void printTasks(String[] tasks, int numberOfTasks) {
        for (int index = 0; index < numberOfTasks; index++) {
            System.out.println((index + 1) + ". " + tasks[index]);
        }
    }
}
