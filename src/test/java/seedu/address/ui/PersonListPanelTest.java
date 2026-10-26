package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxExtension.runOnFxThread;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Region;
import seedu.address.MainApp;
import seedu.address.model.person.Person;
import seedu.address.testutil.JavaFxExtension;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests that contact cards in the list are sized to their content.
 */
@ExtendWith(JavaFxExtension.class)
public class PersonListPanelTest {
    /** Width of the contact list in a 1280-wide window. */
    private static final double LIST_WIDTH = 800;
    private static final double LIST_HEIGHT = 600;
    private static final String LONG_NOTE = "Severe peanut and shellfish allergy, carries an EpiPen in the left pocket"
            + " of her backpack. Parents prefer WhatsApp. Has asthma, inhaler kept at the RA office.";
    /** Lines {@link #LONG_NOTE} needs at {@link #LIST_WIDTH}, with room for font differences across platforms. */
    private static final int MAX_LONG_NOTE_LINES = 3;
    private static final double HEIGHT_TOLERANCE = 1;

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonListPanel(null));
    }

    @Test
    public void card_shortNote_addsAtMostOneLineOfHeight() throws Exception {
        runOnFxThread(() -> {
            Region root = layout(FXCollections.observableArrayList(
                    new PersonBuilder().withName("No Note").build(),
                    new PersonBuilder().withName("Short Note").withNote("Prefers WhatsApp").build()));

            double extraHeight = cellHeight(root, 1) - cellHeight(root, 0);
            assertTrue(extraHeight <= lineHeight(root, 1), "a one-line note should add at most one line");
        });
    }

    @Test
    public void card_longNote_wrapsWithinListWidth() throws Exception {
        runOnFxThread(() -> {
            Region root = layout(FXCollections.observableArrayList(
                    new PersonBuilder().withName("No Note").build(),
                    new PersonBuilder().withName("Long Note").withNote(LONG_NOTE).build()));

            Label note = (Label) cell(root, 1).getGraphic().lookup("#note");
            double extraHeight = cellHeight(root, 1) - cellHeight(root, 0);
            assertTrue(cell(root, 1).getWidth() <= LIST_WIDTH, "card should fit the list without scrolling sideways");
            assertTrue(note.getWidth() <= LIST_WIDTH, "note should wrap within the list");
            assertTrue(extraHeight <= note.getHeight() + HEIGHT_TOLERANCE, () -> "card should have no gap below its"
                    + " note: card is " + extraHeight + " taller, note is " + note.getHeight() + " high");
            assertTrue(note.getHeight() <= lineHeight(root, 1) * MAX_LONG_NOTE_LINES,
                    "a long note should wrap at the list width");
        });
    }

    private static Region layout(ObservableList<Person> persons) {
        Region root = new PersonListPanel(persons).getRoot();
        Scene scene = new Scene(root, LIST_WIDTH, LIST_HEIGHT);
        scene.getStylesheets().add(MainApp.class.getResource("/view/DarkTheme.css").toExternalForm());
        root.applyCss();
        root.layout();
        return root;
    }

    private static ListCell<?> cell(Region root, int index) {
        return root.lookupAll(".list-cell").stream()
                .filter(node -> node instanceof ListCell<?> cell && !cell.isEmpty() && cell.getIndex() == index)
                .map(node -> (ListCell<?>) node)
                .findFirst().orElseThrow(() -> new AssertionError("Contact card was not rendered: " + index));
    }

    private static double cellHeight(Region root, int index) {
        return cell(root, index).getHeight();
    }

    /** Returns the height of one line of a card's small labels, taken from its phone label. */
    private static double lineHeight(Region root, int index) {
        return ((Label) cell(root, index).getGraphic().lookup("#phone")).getHeight();
    }
}
