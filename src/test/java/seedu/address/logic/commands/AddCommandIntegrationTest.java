package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS,
                        validPerson.getName()),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sharedNameOrPhone_success() throws Exception {
        Person existing = model.getAddressBook().getPersonList().getFirst();
        Person sameName = new PersonBuilder(existing).withPhone("12345678").build();
        Person samePhone = new PersonBuilder(existing).withName("Different Name").build();
        new AddCommand(sameName).execute(model);
        new AddCommand(samePhone).execute(model);
        org.junit.jupiter.api.Assertions.assertTrue(model.getAddressBook().getPersonList().contains(sameName));
        org.junit.jupiter.api.Assertions.assertTrue(model.getAddressBook().getPersonList().contains(samePhone));
    }

    @Test
    public void execute_normalizedDuplicateOutsideFilteredList_failure() {
        Person existing = model.getAddressBook().getPersonList().getFirst();
        Person duplicate = new PersonBuilder(existing).withName(existing.getName().fullName.toUpperCase(
                java.util.Locale.ROOT)).withPhone("(" + existing.getPhone().value + ")").build();
        model.updateFilteredPersonList(person -> false);
        assertCommandFailure(new AddCommand(duplicate), model, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

}
