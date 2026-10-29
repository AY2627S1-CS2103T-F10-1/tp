package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxExtension.runOnFxThread;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.UiTestUtil.cellAt;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import seedu.address.logic.commands.FavCommand;
import seedu.address.logic.commands.UnfavCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.JavaFxExtension;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.UiTestUtil;

/**
 * Tests the favourite indicator using real contact cards and the observable list panel.
 */
@ExtendWith(JavaFxExtension.class)
public class PersonCardTest {
    private static final double SCENE_WIDTH = 360;
    private static final double SCENE_HEIGHT = 320;
    private static final Person CONTACT = new PersonBuilder().withRoom("B-204").withNote("Call after 6 pm")
            .withTags("Resident", "Floor 3").build()
            .withEmergencyContact(new EmergencyContact(new Name("Jane Tan"), new Phone("91234567"), "Sister"));

    @Test
    public void constructor_favouriteContact_showsIndicatorAndOtherDetails() throws Exception {
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(CONTACT.withFavourite(true), 2);
            layout(card.getRoot());

            Label favourite = label(card.getRoot(), "favourite");
            assertEquals("★ Favourite", favourite.getText());
            assertTrue(favourite.isVisible());
            assertTrue(favourite.isManaged());
            assertEquals(Color.web("#f2c14e"), favourite.getTextFill());
            assertEquals("2. ", label(card.getRoot(), "id").getText());
            assertEquals(CONTACT.getName().fullName, label(card.getRoot(), "name").getText());
            assertEquals(CONTACT.getPhone().value, label(card.getRoot(), "phone").getText());
            assertEquals("Room: B-204", label(card.getRoot(), "room").getText());
            assertEquals("Note: Call after 6 pm", label(card.getRoot(), "note").getText());
            assertEquals("emergency: Jane Tan(Sister) 91234567", label(card.getRoot(), "emergencyContact").getText());
            FlowPane tags = (FlowPane) card.getRoot().lookup("#tags");
            assertEquals(List.of("Resident", "Floor 3"), tags.getChildren().stream()
                    .map(node -> ((Label) node).getText()).toList());
        });
    }

    @Test
    public void constructor_ordinaryContact_hidesIndicatorWithoutReservingSpace() throws Exception {
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(CONTACT, 1);

            Label favourite = label(card.getRoot(), "favourite");
            assertFalse(favourite.isVisible());
            assertFalse(favourite.isManaged());
            assertTrue(label(card.getRoot(), "room").isVisible());
            assertTrue(label(card.getRoot(), "note").isVisible());
        });
    }

    @Test
    public void listPanel_favAndUnfav_refreshIndicatorAndPreserveOtherDetails() throws Exception {
        runOnFxThread(() -> {
            Model model = new ModelManager();
            model.addPerson(CONTACT);
            PersonListPanel panel = new PersonListPanel(model.getFilteredPersonList());
            Region root = panel.getRoot();
            layout(root);
            assertFalse(label(firstCard(root), "favourite").isVisible());

            assertDoesNotThrow(() -> new FavCommand(INDEX_FIRST_PERSON).execute(model));
            root.layout();
            Label favourite = label(firstCard(root), "favourite");
            assertTrue(favourite.isVisible());
            assertTrue(favourite.isManaged());
            assertEquals(Color.web("#f2c14e"), favourite.getTextFill());
            assertEquals("Note: Call after 6 pm", label(firstCard(root), "note").getText());
            assertEquals("emergency: Jane Tan(Sister) 91234567",
                    label(firstCard(root), "emergencyContact").getText());

            assertDoesNotThrow(() -> new UnfavCommand(INDEX_FIRST_PERSON).execute(model));
            root.layout();
            assertFalse(label(firstCard(root), "favourite").isVisible());
            assertFalse(label(firstCard(root), "favourite").isManaged());
            assertEquals(List.of(CONTACT), model.getAddressBook().getPersonList());
        });
    }

    @Test
    public void listPanel_contactRemoved_doesNotShowPreviousFavouriteIndicator() throws Exception {
        runOnFxThread(() -> {
            Model model = new ModelManager();
            Person favourite = CONTACT.withFavourite(true);
            Person ordinary = new PersonBuilder().withName("Other Contact").build();
            model.addPerson(favourite);
            model.addPerson(ordinary);
            PersonListPanel panel = new PersonListPanel(model.getFilteredPersonList());
            Region root = panel.getRoot();
            layout(root);
            assertTrue(label(firstCard(root), "favourite").isVisible());

            model.deletePerson(favourite);
            root.layout();

            assertEquals("Other Contact", label(firstCard(root), "name").getText());
            assertFalse(label(firstCard(root), "favourite").isVisible());
            assertFalse(label(firstCard(root), "favourite").isManaged());
        });
    }

    private static void layout(Region root) {
        UiTestUtil.layout(root, SCENE_WIDTH, SCENE_HEIGHT);
    }

    private static Label label(Region root, String id) {
        Label label = (Label) root.lookup("#" + id);
        assertNotNull(label, "Missing label: " + id);
        return label;
    }

    private static Region firstCard(Region root) {
        return (Region) cellAt(root, 0).getGraphic();
    }
}
