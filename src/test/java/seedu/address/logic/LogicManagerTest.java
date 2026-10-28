package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListEmergencyCommand;
import seedu.address.logic.commands.ListFavouriteCommand;
import seedu.address.logic.commands.ListSortedCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_EMPTY, model);
    }

    @Test
    public void execute_listSorted_displaysSortedNamesButSavesOriginalOrder() throws Exception {
        Person zara = new PersonBuilder().withName("Zara Tan").build();
        model.addPerson(zara);
        model.addPerson(AMY);
        CommandResult result = logic.execute("list sorted");
        assertEquals(ListSortedCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(List.of(AMY, zara), logic.getFilteredPersonList());
        ReadOnlyAddressBook saved = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow();
        assertEquals(List.of(zara, AMY), saved.getPersonList());
        assertEquals(List.of(zara, AMY), new ModelManager(saved, new UserPrefs()).getFilteredPersonList());
        assertThrows(ParseException.class, ListSortedCommand.MESSAGE_INVALID_FORMAT, () ->
                logic.execute("list sorted extra"));
        assertEquals(List.of(AMY, zara), logic.getFilteredPersonList());
    }

    @Test
    public void execute_favAndUnfav_persistsDisplayedTargetAndPreservesDetails() throws Exception {
        Person otherContact = new PersonBuilder().withName("Other Contact").withRoom("B-204")
                .withNote("Call after 6 pm").withTags("Resident", "Floor 3").build();
        model.addPerson(AMY);
        model.addPerson(otherContact);
        logic.execute("find /name Other Contact");

        logic.execute("fav 1");

        assertEquals(List.of(otherContact.withFavourite(true)), logic.getFilteredPersonList());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook saved = storage.readAddressBook().orElseThrow();
        assertEquals(List.of(AMY, otherContact.withFavourite(true)), saved.getPersonList());

        Model restartedModel = new ModelManager(saved, new UserPrefs());
        Logic restartedLogic = new LogicManager(restartedModel, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"))));
        restartedLogic.execute("unfav 2");

        assertEquals(List.of(AMY, otherContact), restartedLogic.getFilteredPersonList());
        assertEquals(List.of(AMY, otherContact), storage.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void execute_emergencyReplacement_savesNewDetailsAndKeepsGeneralEmergencyListSeparate() throws Exception {
        model.addPerson(new PersonBuilder(AMY).withNote("Allergic to peanuts").build());
        logic.execute("emergency 1 /name Bob Choo /phone 91234567 /relation Brother");
        logic.execute("edit 1 /name Amy Tan /room B-204 /tag /note Call after 6pm");
        logic.execute("emergency 1 /name Alex Yeoh /phone 87654321");

        Person stored = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow().getPersonList().getFirst();
        assertEquals(model.getAddressBook().getPersonList().getFirst(), stored);
        assertEquals("Amy Tan", stored.getName().fullName);
        assertEquals("B-204", stored.getRoom().value);
        assertEquals("Call after 6pm", stored.getNote().value);
        assertEquals("Alex Yeoh", stored.getEmergencyContact().orElseThrow().getName().fullName);
        assertEquals("87654321", stored.getEmergencyContact().orElseThrow().getPhone().value);
        assertEquals("", stored.getEmergencyContact().orElseThrow().getRelation());

        logic.execute("list emerg");
        assertEquals(List.of(), logic.getFilteredPersonList());
        assertEquals(List.of(stored), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_invalidEmergencyReplacement_preservesMemoryAndSavedDetails() throws Exception {
        model.addPerson(AMY);
        logic.execute("emergency 1 /name Bob Choo /phone 91234567 /relation Brother");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook savedBefore = storage.readAddressBook().orElseThrow();

        assertParseException("emergency 1 /name Alex Yeoh /phone abc", Phone.MESSAGE_INVALID_CHARACTERS);
        assertEquals(savedBefore, model.getAddressBook());
        assertEquals(savedBefore, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_listAfterFind_restoresAllContacts() throws Exception {
        Person otherPerson = new PersonBuilder().withName("Other Contact").build();
        model.addPerson(AMY);
        model.addPerson(otherPerson);
        logic.execute("find /name " + AMY.getName().fullName);

        CommandResult result = logic.execute("  list  ");

        assertEquals(ListCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(List.of(AMY, otherPerson), logic.getFilteredPersonList());
        assertEquals(List.of(AMY, otherPerson), model.getAddressBook().getPersonList());
        assertFalse(result.isShowHelp());
        assertFalse(result.isExit());
    }

    @Test
    public void execute_listFavAfterFind_usesAllContactsAndPersistsUnfav() throws Exception {
        Person firstFavourite = new PersonBuilder(AMY).withFavourite(true).withRoom("B-204")
                .withNote("Call after 6 pm").build();
        Person otherContact = new PersonBuilder().withName("Other Contact").build();
        Person secondFavourite = new PersonBuilder().withName("Second Favourite").withFavourite(true).build();
        model.addPerson(firstFavourite);
        model.addPerson(otherContact);
        model.addPerson(secondFavourite);
        logic.execute("find /name Other Contact");

        CommandResult result = logic.execute("list fav");

        assertEquals(ListFavouriteCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(List.of(firstFavourite, secondFavourite), logic.getFilteredPersonList());
        assertEquals(List.of(firstFavourite, otherContact, secondFavourite), model.getAddressBook().getPersonList());

        logic.execute("unfav 1");
        assertEquals(List.of(secondFavourite), logic.getFilteredPersonList());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook saved = storage.readAddressBook().orElseThrow();
        assertEquals(List.of(firstFavourite.withFavourite(false), otherContact, secondFavourite),
                saved.getPersonList());
        Model restartedModel = new ModelManager(saved, new UserPrefs());
        Logic restartedLogic = new LogicManager(restartedModel, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"))));

        restartedLogic.execute("list fav");
        assertEquals(List.of(secondFavourite), restartedLogic.getFilteredPersonList());
        restartedLogic.execute("list");
        assertEquals(saved.getPersonList(), restartedLogic.getFilteredPersonList());
    }

    @Test
    public void execute_invalidListFav_preservesFavouriteViewAndData() throws Exception {
        model.addPerson(AMY.withFavourite(true));
        model.addPerson(new PersonBuilder().withName("Other Contact").build());
        logic.execute("list fav");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(Person::isFavourite);

        for (String input : List.of("list fav 1", "list fav fav", "list fav emerg", "list fav /tag friend")) {
            assertCommandFailure(input, ParseException.class, ListFavouriteCommand.MESSAGE_INVALID_FORMAT,
                    expectedModel);
        }
        assertCommandFailure("list FAV", ParseException.class, ListCommand.MESSAGE_INVALID_FORMAT, expectedModel);
        assertCommandFailure("LIST fav", ParseException.class, MESSAGE_UNKNOWN_COMMAND, expectedModel);
    }

    @Test
    public void execute_invalidList_preservesFilteredList() throws Exception {
        model.addPerson(AMY);
        logic.execute("find /name Nobody");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> false);

        assertCommandFailure("list abc", ParseException.class, ListCommand.MESSAGE_INVALID_FORMAT, expectedModel);
    }

    @Test
    public void execute_listEmergencyAfterFind_filtersAllStoredContacts() throws Exception {
        Person emergencyContact = new PersonBuilder(AMY).withTags("Emergency", "staff").build();
        Person otherContact = new PersonBuilder().withName("Other Contact").build();
        model.addPerson(emergencyContact);
        model.addPerson(otherContact);
        logic.execute("find /name Other Contact");

        CommandResult result = logic.execute("  list   emerg  ");

        assertEquals(ListEmergencyCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(List.of(emergencyContact), logic.getFilteredPersonList());
        assertEquals(List.of(emergencyContact, otherContact), model.getAddressBook().getPersonList());
        ReadOnlyAddressBook savedAddressBook = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow();
        assertEquals(model.getAddressBook(), savedAddressBook);
        assertFalse(result.isShowHelp());
        assertFalse(result.isExit());

        logic.execute("list");
        assertEquals(List.of(emergencyContact, otherContact), logic.getFilteredPersonList());
    }

    @Test
    public void execute_invalidEmergencyList_preservesFilteredList() throws Exception {
        Person emergencyContact = new PersonBuilder(AMY).withTags("emergency").build();
        model.addPerson(emergencyContact);
        model.addPerson(new PersonBuilder().withName("Other Contact").build());
        logic.execute("list emerg");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.equals(emergencyContact));

        assertCommandFailure("list emerg friend", ParseException.class,
                ListEmergencyCommand.MESSAGE_INVALID_FORMAT, expectedModel);
    }

    @Test
    public void execute_wrongCaseEmergencyList_preservesFilteredList() throws Exception {
        Person emergencyContact = new PersonBuilder(AMY).withTags("Emergency").build();
        model.addPerson(emergencyContact);
        model.addPerson(new PersonBuilder().withName("Other Contact").build());
        logic.execute("list emerg");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.equals(emergencyContact));

        assertCommandFailure("LIST emerg", ParseException.class, MESSAGE_UNKNOWN_COMMAND, expectedModel);
        assertCommandFailure("list EMERG", ParseException.class,
                ListCommand.MESSAGE_INVALID_FORMAT, expectedModel);
    }

    @Test
    public void execute_listEmergencyWithoutMatches_showsEmptyMessage() throws Exception {
        model.addPerson(AMY);

        CommandResult result = logic.execute("list emerg");

        assertEquals(ListEmergencyCommand.MESSAGE_EMPTY, result.getFeedbackToUser());
        assertEquals(List.of(), logic.getFilteredPersonList());
        assertEquals(List.of(AMY), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_findTagAndEmergencyList_showSameContacts() throws Exception {
        Person firstEmergency = new PersonBuilder(AMY).withTags("Emergency", "staff").build();
        Person secondEmergency = new PersonBuilder().withName("Security").withTags("EMERGENCY").build();
        model.addPerson(firstEmergency);
        model.addPerson(new PersonBuilder().withName("Other Contact").withTags("staff").build());
        model.addPerson(secondEmergency);

        logic.execute("find /tag emergency");
        List<Person> tagMatches = List.copyOf(logic.getFilteredPersonList());
        assertEquals(List.of(firstEmergency, secondEmergency), tagMatches);

        logic.execute("list emerg");
        assertEquals(tagMatches, logic.getFilteredPersonList());
    }

    @Test
    public void execute_removedListFilter_preservesFilteredList() throws Exception {
        Person emergencyContact = new PersonBuilder(AMY).withTags("Emergency").build();
        model.addPerson(emergencyContact);
        model.addPerson(new PersonBuilder().withName("Other Contact").build());
        logic.execute("find /tag Emergency");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.equals(emergencyContact));

        assertCommandFailure("list /filter Emergency", ParseException.class,
                ListCommand.MESSAGE_INVALID_FORMAT, expectedModel);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + " Amy Bee /phone 11111111";
        Person expectedPerson = new PersonBuilder(AMY).withPhone("11111111").withEmail("").withAddress("")
                .withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
