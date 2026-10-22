package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/** Tests batch selection independently of the displayed results. */
public class ArchiveByTagTest {
    private final AddressBookParser parser = new AddressBookParser();
    private final Person first = new PersonBuilder(ALICE).withTags("Study Group").build();
    private final Person second = new PersonBuilder(BOB).withTags("Study Group", "friends").build();
    private final Person archived = new PersonBuilder(CARL).withTags("Study Group").build().withArchive(true);
    private final Person unrelated = new PersonBuilder(DANIEL).withTags("friends").build();
    private Model model;

    @BeforeEach
    public void setUp() {
        AddressBook book = new AddressBook();
        for (Person person : List.of(first, second, archived, unrelated)) {
            book.addPerson(person);
        }
        model = new ModelManager(book, new UserPrefs());
    }

    @Test
    public void execute_tag_archivesAllActiveMatchesWithoutSkipping() throws Exception {
        CommandResult result = parser.parseCommand("archive /tag Study Group").execute(model);
        assertEquals("Archived 2 contacts.", result.getFeedbackToUser());
        assertEquals(List.of(unrelated), model.getFilteredPersonList());
        assertEquals(List.of(first.withArchive(true), second.withArchive(true), archived, unrelated),
                model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_filteredView_matchesOutsideViewAndPreservesFilter() throws Exception {
        parser.parseCommand("find /name " + first.getName().fullName).execute(model);
        parser.parseCommand("archive /tag Study Group").execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(List.of(first.withArchive(true), second.withArchive(true), archived, unrelated),
                model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_tagMatching_ignoresCaseAndExtraWhitespace() throws Exception {
        parser.parseCommand("archive  /tag   sTuDy   gRoUp  ").execute(model);
        assertEquals(List.of(unrelated), model.getFilteredPersonList());
    }

    @Test
    public void execute_archivedView_keepsViewAndIncludesNewlyArchivedContacts() throws Exception {
        parser.parseCommand("list archived").execute(model);
        parser.parseCommand("archive /tag Study Group").execute(model);
        assertEquals(List.of(first.withArchive(true), second.withArchive(true), archived),
                model.getFilteredPersonList());
    }

    @Test
    public void execute_noMatches_preservesDataAndView() throws Exception {
        parser.parseCommand("find /name " + first.getName().fullName).execute(model);
        CommandResult result = parser.parseCommand("archive /tag unknown").execute(model);
        assertEquals("No active contacts found with that tag. No contacts archived.", result.getFeedbackToUser());
        assertEquals(List.of(first), model.getFilteredPersonList());
        assertEquals(List.of(first, second, archived, unrelated), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_onlyArchivedMatches_reportsNoActiveMatches() throws Exception {
        parser.parseCommand("archive /tag Study Group").execute(model);
        CommandResult result = parser.parseCommand("archive /tag Study Group").execute(model);
        assertEquals("No active contacts found with that tag. No contacts archived.", result.getFeedbackToUser());
    }

    @Test
    public void execute_oneMatch_reportsSingularCount() throws Exception {
        model.deletePerson(second);
        CommandResult result = parser.parseCommand("archive /tag Study Group").execute(model);
        assertEquals("Archived 1 contact.", result.getFeedbackToUser());
    }

    @Test
    public void parse_invalidTagArguments_rejectsWithoutChanges() {
        for (String args : List.of("/tag", "/tag   ", "/tag friends /tag Study Group",
                "1 /tag friends", "/tag friends 1 /name Alice", "/Tag friends", "/unknown friends")) {
            assertThrows(ParseException.class, () -> parser.parseCommand("archive " + args));
            assertEquals(List.of(first, second, archived, unrelated), model.getAddressBook().getPersonList());
        }
    }
}
