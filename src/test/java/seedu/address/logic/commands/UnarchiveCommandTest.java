package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/** Tests restoration through the real parser and observable model. */
public class UnarchiveCommandTest {
    private final AddressBookParser parser = new AddressBookParser();
    private final Person active = new PersonBuilder(ALICE).withTags("Study Group").build();
    private final Person first = new PersonBuilder(BOB).withTags("Study Group").withRoom("B-204")
            .build().withArchive(true);
    private final Person second = new PersonBuilder(CARL).withTags("Study Group").build().withArchive(true);
    private final Person unrelated = new PersonBuilder(DANIEL).withTags("friends").build().withArchive(true);
    private Model model;

    @BeforeEach
    public void setUp() {
        AddressBook book = new AddressBook();
        for (Person person : List.of(active, first, second, unrelated)) {
            book.addPerson(person);
        }
        model = new ModelManager(book, new UserPrefs());
    }

    @Test
    public void execute_index_restoresDisplayedContactAndPreservesArchivedView() throws Exception {
        parser.parseCommand("list archived").execute(model);
        ObservableList<Person> displayed = model.getFilteredPersonList();
        CommandResult result = parser.parseCommand("  unarchive   1  ").execute(model);
        assertEquals("Unarchived 1 contact.", result.getFeedbackToUser());
        assertSame(displayed, model.getFilteredPersonList());
        assertEquals(List.of(second, unrelated), displayed);
        assertEquals(List.of(active, first.withArchive(false), second, unrelated),
                model.getAddressBook().getPersonList());
        parser.parseCommand("list").execute(model);
        assertEquals(List.of(active, first.withArchive(false)), displayed);
    }

    @Test
    public void execute_repeatedIndex_usesUpdatedDisplayedIndices() throws Exception {
        parser.parseCommand("list archived").execute(model);
        parser.parseCommand("unarchive 1").execute(model);
        parser.parseCommand("unarchive 1").execute(model);
        assertEquals(List.of(unrelated), model.getFilteredPersonList());
        assertEquals(List.of(active, first.withArchive(false), second.withArchive(false), unrelated),
                model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_activeContact_reportsNoChange() throws Exception {
        CommandResult result = parser.parseCommand("unarchive 1").execute(model);
        assertEquals("Contact is already active. No contacts unarchived.", result.getFeedbackToUser());
        assertEquals(List.of(active), model.getFilteredPersonList());
        assertEquals(List.of(active, first, second, unrelated), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_invalidDisplayedIndex_preservesDataAndView() {
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                parser.parseCommand("unarchive 2").execute(model));
        assertEquals(List.of(active), model.getFilteredPersonList());
        assertEquals(List.of(active, first, second, unrelated), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_emptyList_rejectsIndex() {
        model = new ModelManager();
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                parser.parseCommand("unarchive 1").execute(model));
    }

    @Test
    public void execute_tag_restoresAllArchivedMatchesAndPreservesFilter() throws Exception {
        parser.parseCommand("find /name " + active.getName().fullName).execute(model);
        CommandResult result = parser.parseCommand("unarchive /tag  sTuDy   gRoUp ").execute(model);
        assertEquals("Unarchived 2 contacts.", result.getFeedbackToUser());
        assertEquals(List.of(active), model.getFilteredPersonList());
        assertEquals(List.of(active, first.withArchive(false), second.withArchive(false), unrelated),
                model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_tagInArchivedView_doesNotSkipContactsAsListShrinks() throws Exception {
        parser.parseCommand("list archived").execute(model);
        parser.parseCommand("unarchive /tag Study Group").execute(model);
        assertEquals(List.of(unrelated), model.getFilteredPersonList());
    }

    @Test
    public void execute_tagOneMatch_reportsSingularCount() throws Exception {
        CommandResult result = parser.parseCommand("unarchive /tag friends").execute(model);
        assertEquals("Unarchived 1 contact.", result.getFeedbackToUser());
        assertEquals(List.of(active, first, second, unrelated.withArchive(false)),
                model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_noArchivedMatches_reportsNoChange() throws Exception {
        model.setPerson(first, first.withArchive(false));
        model.setPerson(second, second.withArchive(false));
        for (String tag : List.of("unknown", "Study Group")) {
            CommandResult result = parser.parseCommand("unarchive /tag " + tag).execute(model);
            assertEquals("No archived contacts found with that tag. No contacts unarchived.",
                    result.getFeedbackToUser());
            assertEquals(List.of(active, first.withArchive(false), second.withArchive(false), unrelated),
                    model.getAddressBook().getPersonList());
        }
    }

    @Test
    public void parse_invalidArguments_rejectedWithoutChanges() {
        for (String args : List.of("", "0", "-1", "+1", "1.5", "2147483648", "one", "1 2", "/tag",
                "/tag friends /tag Study Group", "1 /tag friends", "/tag friends /name Alice", "/Tag friends")) {
            assertThrows(ParseException.class, () -> parser.parseCommand("unarchive " + args));
            assertEquals(List.of(active, first, second, unrelated), model.getAddressBook().getPersonList());
        }
    }
}
