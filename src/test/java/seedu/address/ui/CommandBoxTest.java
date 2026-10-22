package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;

public class CommandBoxTest {
    private static final int FX_TIMEOUT_SECONDS = 10;

    @BeforeAll
    public static void initializeJavaFx() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            started.countDown();
        });
        assertTrue(started.await(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS), "JavaFX did not start in time");
    }

    @AfterAll
    public static void stopJavaFx() {
        Platform.exit();
    }

    @Test
    public void navigate_emptyHistory_preservesInput() throws Exception {
        runOnFxThread(() -> {
            Fixture fixture = new Fixture();
            fixture.commandTextField.setText("unfinished input");

            fixture.press(KeyCode.UP);
            assertEquals("unfinished input", fixture.commandTextField.getText());
            fixture.press(KeyCode.DOWN);
            assertEquals("unfinished input", fixture.commandTextField.getText());
            assertTrue(fixture.executedCommands.isEmpty());
        });
    }

    @Test
    public void navigate_submittedCommands_displaysHistoryWithoutExecuting() throws Exception {
        runOnFxThread(() -> {
            Fixture fixture = new Fixture();
            fixture.submit("list");
            fixture.submit("find /tag Resident");

            fixture.press(KeyCode.UP);
            assertEquals("find /tag Resident", fixture.commandTextField.getText());
            assertEquals(fixture.commandTextField.getLength(), fixture.commandTextField.getCaretPosition());
            fixture.press(KeyCode.UP);
            assertEquals("list", fixture.commandTextField.getText());
            fixture.press(KeyCode.UP);
            assertEquals("list", fixture.commandTextField.getText());
            fixture.press(KeyCode.DOWN);
            assertEquals("find /tag Resident", fixture.commandTextField.getText());
            assertEquals(List.of("list", "find /tag Resident"), fixture.executedCommands);
        });
    }

    @Test
    public void navigate_editedRecall_preservesOriginalDraftAndHistory() throws Exception {
        runOnFxThread(() -> {
            Fixture fixture = new Fixture();
            fixture.submit("find /tag Resident");
            String draft = "  edit 1 /room B-  ";
            fixture.commandTextField.setText(draft);

            fixture.press(KeyCode.UP);
            fixture.commandTextField.setText("find /tag Staff");
            fixture.press(KeyCode.DOWN);
            assertEquals(draft, fixture.commandTextField.getText());
            fixture.press(KeyCode.DOWN);
            assertEquals(draft, fixture.commandTextField.getText());
            fixture.press(KeyCode.UP);
            assertEquals("find /tag Resident", fixture.commandTextField.getText());
            assertEquals(List.of("find /tag Resident"), fixture.executedCommands);
        });
    }

    @Test
    public void enter_editedRecall_executesOnceAndResetsNavigation() throws Exception {
        runOnFxThread(() -> {
            Fixture fixture = new Fixture();
            fixture.submit("list");
            assertEquals("", fixture.commandTextField.getText());
            fixture.commandTextField.setText("old draft");
            fixture.press(KeyCode.UP);
            fixture.commandTextField.setText("help");

            fixture.press(KeyCode.ENTER);

            assertEquals(List.of("list", "help"), fixture.executedCommands);
            assertEquals("", fixture.commandTextField.getText());
            fixture.press(KeyCode.UP);
            assertEquals("help", fixture.commandTextField.getText());
            fixture.press(KeyCode.DOWN);
            assertEquals("", fixture.commandTextField.getText());
        });
    }

    @Test
    public void enter_parseFailure_retainsInputAndRecordsCommand() throws Exception {
        runOnFxThread(() -> assertFailedSubmissionIsRecalled(new Fixture(command -> {
            throw new ParseException("Invalid command format");
        })));
    }

    @Test
    public void enter_executionFailure_retainsInputAndRecordsCommand() throws Exception {
        runOnFxThread(() -> assertFailedSubmissionIsRecalled(new Fixture(command -> {
            throw new CommandException("Invalid contact index");
        })));
    }

    @Test
    public void enter_blankInput_doesNotExecuteOrRecord() throws Exception {
        runOnFxThread(() -> {
            Fixture fixture = new Fixture();
            fixture.submit("list");
            for (String blank : List.of("", "   ", "\u2003")) {
                fixture.submit(blank);
            }
            assertEquals(List.of("list"), fixture.executedCommands);

            fixture.commandTextField.clear();
            fixture.press(KeyCode.UP);
            assertEquals("list", fixture.commandTextField.getText());
            fixture.press(KeyCode.UP);
            fixture.press(KeyCode.DOWN);
            assertEquals("", fixture.commandTextField.getText());
        });
    }

    @Test
    public void navigate_modifiedArrowsAndOtherControls_doesNotRecall() throws Exception {
        runOnFxThread(() -> {
            Fixture fixture = new Fixture();
            fixture.submit("list");
            fixture.commandTextField.setText("draft");
            for (int modifier = 0; modifier < 4; modifier++) {
                for (KeyCode arrow : List.of(KeyCode.UP, KeyCode.DOWN)) {
                    press(fixture.commandTextField, arrow,
                            modifier == 0, modifier == 1, modifier == 2, modifier == 3);
                    assertEquals("draft", fixture.commandTextField.getText());
                }
            }

            fixture.commandTextField.positionCaret(0);
            fixture.press(KeyCode.RIGHT);
            assertEquals(1, fixture.commandTextField.getCaretPosition());
            press(fixture.otherTextField, KeyCode.UP, false, false, false, false);
            press(fixture.otherTextField, KeyCode.DOWN, false, false, false, false);
            assertEquals("draft", fixture.commandTextField.getText());
            assertEquals(List.of("list"), fixture.executedCommands);
        });
    }

    @Test
    public void navigate_repeatedSubmissions_keepsSeparateEntries() throws Exception {
        runOnFxThread(() -> {
            Fixture fixture = new Fixture();
            fixture.submit("list");
            fixture.submit("list");
            fixture.commandTextField.setText("draft");

            fixture.press(KeyCode.UP);
            fixture.press(KeyCode.UP);
            fixture.press(KeyCode.DOWN);
            assertEquals("list", fixture.commandTextField.getText());
            fixture.press(KeyCode.DOWN);
            assertEquals("draft", fixture.commandTextField.getText());
            assertEquals(List.of("list", "list"), fixture.executedCommands);
        });
    }

    @Test
    public void enter_recalledIndexCommand_usesCurrentDisplayedList() throws Exception {
        runOnFxThread(() -> {
            ModelManager model = new ModelManager();
            AddressBookParser parser = new AddressBookParser();
            Fixture fixture = new Fixture(command -> parser.parseCommand(command).execute(model));
            fixture.submit("add Alice /phone 91234567");
            fixture.submit("add Bob /phone 87654321");
            fixture.submit("delete 2");
            fixture.submit("add Carol /phone 98765432");

            fixture.press(KeyCode.UP);
            fixture.press(KeyCode.UP);
            assertEquals("delete 2", fixture.commandTextField.getText());
            assertEquals(2, model.getFilteredPersonList().size());
            assertEquals("Carol", model.getFilteredPersonList().get(1).getName().fullName);

            fixture.press(KeyCode.ENTER);

            assertEquals(1, model.getFilteredPersonList().size());
            assertEquals("Alice", model.getFilteredPersonList().getFirst().getName().fullName);
        });
    }

    private static void assertFailedSubmissionIsRecalled(Fixture fixture) {
        fixture.submit("failed command");
        assertEquals("failed command", fixture.commandTextField.getText());
        assertTrue(fixture.commandTextField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));

        fixture.commandTextField.clear();
        assertFalse(fixture.commandTextField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
        fixture.press(KeyCode.UP);
        assertEquals("failed command", fixture.commandTextField.getText());
        assertEquals(List.of("failed command"), fixture.executedCommands);
    }

    private static void runOnFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private static void press(TextField textField, KeyCode code,
            boolean isShiftDown, boolean isControlDown, boolean isAltDown, boolean isMetaDown) {
        Event.fireEvent(textField, new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code,
                isShiftDown, isControlDown, isAltDown, isMetaDown));
    }

    /** Creates real command-box controls with an executor that records submissions. */
    private static class Fixture {
        private final List<String> executedCommands = new ArrayList<>();
        private final TextField commandTextField;
        private final TextField otherTextField = new TextField();

        Fixture() {
            this(command -> new CommandResult("Success"));
        }

        Fixture(CommandBox.CommandExecutor executor) {
            CommandBox commandBox = new CommandBox(command -> {
                executedCommands.add(command);
                return executor.execute(command);
            });
            commandTextField = (TextField) commandBox.getRoot().lookup("#commandTextField");
            Scene scene = new Scene(new VBox(commandBox.getRoot(), otherTextField), 500, 180);
            scene.getRoot().applyCss();
            scene.getRoot().layout();
        }

        void submit(String command) {
            commandTextField.setText(command);
            press(KeyCode.ENTER);
        }

        void press(KeyCode code) {
            CommandBoxTest.press(commandTextField, code, false, false, false, false);
        }
    }
}
