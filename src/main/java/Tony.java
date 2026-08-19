import java.util.Scanner;

/**
 * Starts the Tony chatbot application.
 */
public class Tony {
    /**
     * Displays Tony's greeting, echoes each entered command, and exits when the
     * user enters {@code bye}.
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
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(line);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }

            System.out.println(command);
            System.out.println(line);
        }
    }
}
