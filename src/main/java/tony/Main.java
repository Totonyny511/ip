package tony;

import java.util.Objects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays Tony's graphical chat interface.
 */
public class Main extends Application {
    /** Initial width of the application window. */
    private static final double WINDOW_WIDTH = 620;

    /** Initial height of the application window. */
    private static final double WINDOW_HEIGHT = 720;

    /** Contains the messages in conversation order. */
    private final VBox dialogContainer = new VBox();

    /** Accepts commands from the user. */
    private final TextField userInput = new TextField();

    /** Sends the current command to Tony. */
    private final Button sendButton = new Button("Send");

    /** Processes commands and manages persisted tasks. */
    private Tony tony;

    /**
     * Builds and displays the chatbot window.
     *
     * @param stage primary JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        tony = new Tony();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(createHeader());
        root.setCenter(createConversation());
        root.setBottom(createComposer());

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        String stylesheet = Objects.requireNonNull(
                getClass().getResource("/css/main.css"), "Missing GUI stylesheet").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        stage.setTitle("Tony");
        stage.setMinWidth(440);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();

        showWelcomeMessage();
        Platform.runLater(userInput::requestFocus);
    }

    /** Creates the heading and concise command guidance. */
    private VBox createHeader() {
        Label title = new Label("Tony");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Your personal task chatbot");
        subtitle.getStyleClass().add("app-subtitle");

        Label commandHint = new Label(
                "Try: todo, deadline, event, list, find, mark, unmark, delete, or bye");
        commandHint.setWrapText(true);
        commandHint.getStyleClass().add("command-hint");

        VBox header = new VBox(2, title, subtitle, commandHint);
        header.getStyleClass().add("app-header");
        return header;
    }

    /** Creates the scrollable conversation area. */
    private ScrollPane createConversation() {
        dialogContainer.setPadding(new Insets(18));
        dialogContainer.setSpacing(12);
        dialogContainer.getStyleClass().add("dialog-container");

        ScrollPane scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("conversation-scroll");
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
        return scrollPane;
    }

    /** Creates the text field and send button used to submit commands. */
    private HBox createComposer() {
        userInput.setPromptText("Type a command...");
        userInput.setAccessibleText("Chatbot command");
        userInput.setOnAction(event -> handleUserInput());
        HBox.setHgrow(userInput, Priority.ALWAYS);

        sendButton.setDefaultButton(true);
        sendButton.setAccessibleText("Send command");
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                userInput.getText().isBlank(), userInput.textProperty()));
        sendButton.setOnAction(event -> handleUserInput());

        HBox composer = new HBox(10, userInput, sendButton);
        composer.setPadding(new Insets(14));
        composer.getStyleClass().add("composer");
        return composer;
    }

    /** Displays the opening prompt and any storage warning. */
    private void showWelcomeMessage() {
        String welcomeMessage = "What can I do for you?";
        if (!tony.getStartupMessage().isEmpty()) {
            welcomeMessage += "\n\n" + tony.getStartupMessage();
        }
        dialogContainer.getChildren().add(DialogBox.getTonyDialog(welcomeMessage));
    }

    /** Sends the entered command to Tony and adds both sides of the exchange. */
    private void handleUserInput() {
        String command = userInput.getText();
        if (command.isBlank()) {
            return;
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(command),
                DialogBox.getTonyDialog(tony.getResponse(command)));
        userInput.clear();

        if (Tony.isExitCommand(command)) {
            userInput.setPromptText("Conversation ended");
            userInput.setDisable(true);
            sendButton.disableProperty().unbind();
            sendButton.setDisable(true);
        }
    }
}
