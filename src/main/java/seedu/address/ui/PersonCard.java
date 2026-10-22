package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final String ROOM_LABEL_PREFIX = "Room: ";
    private static final String NOTE_LABEL_PREFIX = "Note: ";
    private static final String FAVOURITE_LABEL_TEXT = "★ Favourite";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private FlowPane tags;
    @FXML
    private Label room;
    @FXML
    private Label note;
    @FXML
    private Label favourite;
    @FXML
    private Label emergencyContact;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        showIfPresent(favourite, "", person.isFavourite() ? FAVOURITE_LABEL_TEXT : "");
        showIfPresent(room, ROOM_LABEL_PREFIX, person.getRoom().value);
        showIfPresent(note, NOTE_LABEL_PREFIX, person.getNote().value);
        showIfPresent(emergencyContact, "emergency: ",
                person.getEmergencyContact().map(Object::toString).orElse(""));
        person.getTags().forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

    /** Shows {@code value} after {@code prefix} in {@code label}, or hides the label if there is no value. */
    private static void showIfPresent(Label label, String prefix, String value) {
        assert label != null && prefix != null && value != null : "every card label and value is set";
        boolean isPresent = !value.isEmpty();
        label.setText(prefix + value);
        label.setVisible(isPresent);
        label.setManaged(isPresent);
    }
}
