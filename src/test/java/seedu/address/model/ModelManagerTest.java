package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameEqualsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.AddressBookBuilder;

public class ModelManagerTest {

    /** Orders persons by the length of their full name, shortest first. */
    private static final Comparator<Person> NAME_LENGTH_ORDER =
            Comparator.comparingInt(person -> person.getName().fullName.length());
    /** Persons in the order they are added to the address book, which NAME_LENGTH_ORDER changes. */
    private static final List<Person> ORIGINAL_ORDER = List.of(ALICE, BENSON, CARL, DANIEL);

    private ModelManager modelManager = new ModelManager();

    @Test
    public void equals_differentDisplayOrder_returnsFalse() {
        modelManager.addPerson(BENSON);
        modelManager.addPerson(ALICE);
        ModelManager copy = new ModelManager(modelManager.getAddressBook(), new UserPrefs());
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS,
                Comparator.comparing(person -> person.getName().fullName));
        assertFalse(modelManager.equals(copy));
        assertEquals(modelManager.getAddressBook(), copy.getAddressBook());
    }

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void updateFilteredPersonList_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.updateFilteredPersonList(null));
        assertThrows(NullPointerException.class, () ->
                modelManager.updateFilteredPersonList(null, NAME_LENGTH_ORDER));
        assertThrows(NullPointerException.class, () ->
                modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS, null));
    }

    @Test
    public void updateFilteredPersonList_withComparator_sortsKeepingTiesInOrder() {
        modelManager = getModelOf(ORIGINAL_ORDER);
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS, NAME_LENGTH_ORDER);

        // Benson Meier and Daniel Meier have names of the same length
        assertEquals(List.of(CARL, BENSON, DANIEL, ALICE), modelManager.getFilteredPersonList());
    }

    @Test
    public void updateFilteredPersonList_withoutComparator_restoresOriginalOrder() {
        modelManager = getModelOf(ORIGINAL_ORDER);
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS, NAME_LENGTH_ORDER);
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        assertEquals(ORIGINAL_ORDER, modelManager.getFilteredPersonList());
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        modelManager.updateFilteredPersonList(new NameEqualsPredicate(ALICE.getName().fullName));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // same persons in a different order -> returns false
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS, NAME_LENGTH_ORDER);
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }

    /**
     * Returns a model whose address book holds {@code persons}, in order, with default user prefs.
     */
    private static ModelManager getModelOf(List<Person> persons) {
        AddressBookBuilder builder = new AddressBookBuilder();
        persons.forEach(builder::withPerson);
        return new ModelManager(builder.build(), new UserPrefs());
    }
}
