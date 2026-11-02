package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/** Verifies archive workflows across the real logic and JSON storage boundary. */
public class ArchivePersistenceTest {
    @TempDir
    public Path temporaryFolder;

    private Logic createLogic(Model model, JsonAddressBookStorage storage) {
        return new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
    }

    @Test
    public void unicodeOnlyTag_rejectedWithoutChangingDataViewOrFile() throws Exception {
        AddressBook book = new AddressBook();
        book.addPerson(ALICE);
        book.addPerson(BOB.withArchive(true));
        Model model = new ModelManager(book, new UserPrefs());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("contacts.json"));
        Logic logic = createLogic(model, storage);
        logic.execute("list archived");
        List<Person> displayed = List.copyOf(model.getFilteredPersonList());

        for (String command : List.of("archive", "unarchive", "find")) {
            for (String blank : List.of("\u00a0", "\u2003", "\u202f")) {
                assertThrows(ParseException.class, () -> logic.execute(command + " /tag " + blank));
                assertEquals(book, model.getAddressBook());
                assertEquals(displayed, model.getFilteredPersonList());
                assertEquals(book, storage.readAddressBook().orElseThrow());
            }
        }
    }

    @Test
    public void archiveAndRestore_reloadAfterEachMutation_preservesContacts() throws Exception {
        EmergencyContact emergency = new EmergencyContact(new Name("Jane Tan"), new Phone("91234567"), "Sister");
        Person first = new PersonBuilder(ALICE).withTags("friends").withRoom("B-204")
                .withNote("Call after six").build().withEmergencyContact(emergency);
        Person second = new PersonBuilder(BOB).withTags("friends").withNote("Prefers text messages").build()
                .withEmergencyContact(new EmergencyContact(new Name("John Tan"), new Phone("98765432"), "Brother"));
        AddressBook book = new AddressBook();
        book.addPerson(first);
        book.addPerson(second);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("contacts.json"));
        Model model = new ModelManager(book, new UserPrefs());
        Logic logic = createLogic(model, storage);

        logic.execute("archive 1");
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(List.of(second), model.getFilteredPersonList());
        Person savedFirst = model.getAddressBook().getPersonList().get(0);
        assertEquals(first.getNote(), savedFirst.getNote());
        assertEquals(first.getEmergencyContact(), savedFirst.getEmergencyContact());
        logic = createLogic(model, storage);
        logic.execute("archive /tag friends");

        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(List.of(), model.getFilteredPersonList());
        logic = createLogic(model, storage);
        logic.execute("list archived");
        assertEquals(List.of(first.withArchive(true), second.withArchive(true)), model.getFilteredPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        logic.execute("unarchive 1");

        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(List.of(first), model.getFilteredPersonList());
        logic = createLogic(model, storage);
        logic.execute("unarchive /tag friends");
        assertEquals(book, storage.readAddressBook().orElseThrow());
        Model reloaded = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(List.of(first, second), reloaded.getFilteredPersonList());
    }

    @Test
    public void saveFailure_archiveAndRestore_keepMemoryChangesAndPreviousDiskState() throws Exception {
        for (boolean initiallyArchived : new boolean[] {false, true}) {
            for (IOException failure : List.of(new IOException("disk failure"),
                    new AccessDeniedException("contacts.json"))) {
                Person original = ALICE.withArchive(initiallyArchived);
                AddressBook book = new AddressBook();
                book.addPerson(original);
                Path path = temporaryFolder.resolve("failure.json");
                JsonAddressBookStorage realStorage = new JsonAddressBookStorage(path);
                realStorage.saveAddressBook(book);
                JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(path) {
                    @Override
                    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                        throw failure;
                    }
                };
                Model model = new ModelManager(book, new UserPrefs());
                if (initiallyArchived) {
                    model.updateFilteredPersonList(Person::isArchived);
                }
                Logic logic = createLogic(model, failingStorage);
                String expectedMessage = String.format(failure instanceof AccessDeniedException
                        ? LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT : LogicManager.FILE_OPS_ERROR_FORMAT,
                        failure.getMessage());
                assertThrows(CommandException.class, expectedMessage, () ->
                        logic.execute(initiallyArchived ? "unarchive 1" : "archive 1"));
                assertEquals(List.of(ALICE.withArchive(!initiallyArchived)),
                        model.getAddressBook().getPersonList());
                assertEquals(List.of(), model.getFilteredPersonList());
                assertEquals(book, realStorage.readAddressBook().orElseThrow());
            }
        }
    }
}
