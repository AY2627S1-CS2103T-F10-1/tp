package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.EmergencyContactPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class ListEmergencyCommandTest {

    private static final Person FIRST_EMERGENCY_CONTACT = new PersonBuilder().withName("On Call")
            .withTags("staff", "Emergency").build();
    private static final Person SECOND_EMERGENCY_CONTACT = new PersonBuilder().withName("Security")
            .withTags("EMERGENCY").build();
    private static final Person OTHER_CONTACT = new PersonBuilder().withName("Resident")
            .withTags("emerg", "emergencyservice").build();

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addPerson(FIRST_EMERGENCY_CONTACT);
        model.addPerson(OTHER_CONTACT);
        model.addPerson(SECOND_EMERGENCY_CONTACT);
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(new EmergencyContactPredicate());
    }

    @Test
    public void execute_unfilteredList_showsOnlyEmergencyContactsInStoredOrder() {
        assertCommandSuccess(new ListEmergencyCommand(), model, ListEmergencyCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(List.of(FIRST_EMERGENCY_CONTACT, SECOND_EMERGENCY_CONTACT), model.getFilteredPersonList());
    }

    @Test
    public void execute_filteredList_searchesAllStoredContacts() {
        model.updateFilteredPersonList(person -> person.equals(OTHER_CONTACT));
        assertCommandSuccess(new ListEmergencyCommand(), model, ListEmergencyCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_noEmergencyContacts_showsEmptyListAndMessage() {
        model = new ModelManager();
        model.addPerson(OTHER_CONTACT);
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> false);
        assertCommandSuccess(new ListEmergencyCommand(), model, ListEmergencyCommand.MESSAGE_EMPTY, expectedModel);
    }

    @Test
    public void execute_emptyAddressBook_showsEmptyListAndMessage() {
        model = new ModelManager();
        expectedModel = new ModelManager();
        assertCommandSuccess(new ListEmergencyCommand(), model, ListEmergencyCommand.MESSAGE_EMPTY, expectedModel);
    }

    @Test
    public void execute_repeatedCommand_showsSameList() {
        new ListEmergencyCommand().execute(model);
        assertCommandSuccess(new ListEmergencyCommand(), model, ListEmergencyCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listAfterEmergency_restoresAllContacts() {
        new ListEmergencyCommand().execute(model);
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_tagsChanged_updatesEmergencyList() {
        new ListEmergencyCommand().execute(model);
        Person newlyTaggedContact = new PersonBuilder(OTHER_CONTACT).withTags("Emergency").build();
        model.setPerson(OTHER_CONTACT, newlyTaggedContact);
        assertEquals(List.of(FIRST_EMERGENCY_CONTACT, newlyTaggedContact, SECOND_EMERGENCY_CONTACT),
                model.getFilteredPersonList());

        Person noLongerEmergencyContact = new PersonBuilder(FIRST_EMERGENCY_CONTACT).withTags("staff").build();
        model.setPerson(FIRST_EMERGENCY_CONTACT, noLongerEmergencyContact);
        assertEquals(List.of(newlyTaggedContact, SECOND_EMERGENCY_CONTACT), model.getFilteredPersonList());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListEmergencyCommand().execute(null));
    }
}
