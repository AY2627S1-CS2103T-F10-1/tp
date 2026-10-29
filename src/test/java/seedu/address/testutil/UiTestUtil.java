package seedu.address.testutil;

import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Region;
import seedu.address.MainApp;

/**
 * Helper methods for tests that lay out UI parts. They must be called on the JavaFX thread.
 */
public class UiTestUtil {
    private static final String THEME = "/view/DarkTheme.css";

    /** Lays out {@code root} in a scene of the given size, styled with the app's theme, and returns it. */
    public static Region layout(Region root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(MainApp.class.getResource(THEME).toExternalForm());
        root.applyCss();
        root.layout();
        return root;
    }

    /** Returns the rendered list cell at the zero-based {@code index} in the list under {@code root}. */
    public static ListCell<?> cellAt(Region root, int index) {
        return root.lookupAll(".list-cell").stream()
                .filter(node -> node instanceof ListCell<?> cell && !cell.isEmpty() && cell.getIndex() == index)
                .map(node -> (ListCell<?>) node)
                .findFirst().orElseThrow(() -> new AssertionError("List cell was not rendered: " + index));
    }
}
