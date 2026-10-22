package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.address.logic.commands.FavCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/** Tests favourite, archive and emergency details together through real commands and JSON storage. */
public class FavouriteArchiveIntegrationTest {
    private static final EmergencyContact EMERGENCY =
            new EmergencyContact(new Name("Jane Tan"), new Phone("91234567"), "Sister");
    private static final Person FAVOURITE = new PersonBuilder(AMY).withFavourite(true).withRoom("B-204")
            .withNote("Call after six").withTags("Resident").build().withEmergencyContact(EMERGENCY);
    private static final Person OTHER = new PersonBuilder().withName("Other Contact").build();

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() throws Exception {
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("contacts.json"));
        AddressBook book = new AddressBook();
        book.addPerson(FAVOURITE);
        book.addPerson(OTHER);
        storage.saveAddressBook(book);
        restart();
    }

    @Test
    public void archiveAndRestore_favouriteSurvivesRestartsAndReturnsToFavouriteList() throws Exception {
        logic.execute("list fav");
        logic.execute("archive 1");
        assertEquals(List.of(), logic.getFilteredPersonList());
        assertEquals(FAVOURITE.withArchive(true), model.getAddressBook().getPersonList().getFirst());

        restart();
        assertEquals(List.of(OTHER), logic.getFilteredPersonList());
        logic.execute("list fav");
        assertEquals(List.of(), logic.getFilteredPersonList());
        logic.execute("list archived");
        assertEquals(List.of(FAVOURITE.withArchive(true)), logic.getFilteredPersonList());
        logic.execute("unarchive 1");

        restart();
        logic.execute("list fav");
        assertEquals(List.of(FAVOURITE), logic.getFilteredPersonList());
        assertEquals(List.of(FAVOURITE, OTHER), storage.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void favAndUnfav_archivedContact_rejectedRegardlessOfExistingStatus() throws Exception {
        for (boolean isFavourite : new boolean[] {false, true}) {
            Person archived = FAVOURITE.withFavourite(isFavourite).withArchive(true);
            AddressBook book = new AddressBook();
            book.addPerson(OTHER);
            book.addPerson(archived);
            storage.saveAddressBook(book);
            restart();
            logic.execute("list archived");
            String savedBefore = Files.readString(storage.getAddressBookFilePath());

            for (String command : List.of("fav 1", "unfav 1")) {
                assertThrows(CommandException.class, FavCommand.MESSAGE_ARCHIVED_PERSON, () -> logic.execute(command));
                assertEquals(List.of(archived), logic.getFilteredPersonList());
                assertEquals(List.of(OTHER, archived), model.getAddressBook().getPersonList());
                assertEquals(savedBefore, Files.readString(storage.getAddressBookFilePath()));
            }
        }
    }

    @Test
    public void changingFavouriteAndEmergencyAndEditing_preservesOtherDetailsAfterRestart() throws Exception {
        logic.execute("unfav 1");
        assertEquals(FAVOURITE.withFavourite(false), model.getAddressBook().getPersonList().getFirst());
        logic.execute("fav 1");
        logic.execute("emergency 1 /name John Tan /phone 87654321");
        EmergencyContact replacement = new EmergencyContact(new Name("John Tan"), new Phone("87654321"), "");
        Person expected = new PersonBuilder(FAVOURITE).withNote("").withTags().build()
                .withEmergencyContact(replacement);
        logic.execute("edit 1 /note /tag");

        restart();
        assertEquals(List.of(expected, OTHER), model.getAddressBook().getPersonList());
        logic.execute("list fav");
        assertEquals(List.of(expected), logic.getFilteredPersonList());
    }

    @Test
    public void listFavourites_archivedViewAndTagRestore_showsOnlyActiveFavouritesInStoredOrder() throws Exception {
        Person secondFavourite = new PersonBuilder(OTHER).withFavourite(true).withTags("Resident").build();
        model.setPerson(OTHER, secondFavourite);
        logic.execute("archive /tag Resident");
        restart();
        logic.execute("list archived");
        logic.execute("list fav");
        assertEquals(List.of(), logic.getFilteredPersonList());
        logic.execute("unarchive /tag Resident");
        logic.execute("list fav");
        assertEquals(List.of(FAVOURITE, secondFavourite), logic.getFilteredPersonList());
        assertEquals(List.of(FAVOURITE, secondFavourite), storage.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void legacyFiles_missingEitherStatus_preserveOtherFields() throws Exception {
        logic.execute("archive 1");
        String json = Files.readString(storage.getAddressBookFilePath());
        ObjectMapper mapper = new ObjectMapper();
        for (String field : List.of("archive", "isFavourite")) {
            JsonNode legacy = mapper.readTree(json);
            for (JsonNode person : legacy.get("persons")) {
                ObjectNode contact = (ObjectNode) person;
                contact.remove(field);
            }
            Files.writeString(storage.getAddressBookFilePath(), mapper.writeValueAsString(legacy));
            restart();

            Person restored = model.getAddressBook().getPersonList().getFirst();
            assertEquals(FAVOURITE.getEmergencyContact(), restored.getEmergencyContact());
            assertEquals(FAVOURITE.getNote(), restored.getNote());
            if (field.equals("archive")) {
                assertFalse(restored.isArchived());
                assertTrue(restored.isFavourite());
            } else {
                assertTrue(restored.isArchived());
                assertFalse(restored.isFavourite());
            }
        }
    }

    private void restart() throws Exception {
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
    }
}
