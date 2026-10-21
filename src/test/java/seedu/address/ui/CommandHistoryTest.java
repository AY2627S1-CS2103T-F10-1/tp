package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {
    private final CommandHistory history = new CommandHistory();

    @Test
    public void constructor_newHistory_isEmpty() {
        assertEquals(List.of(), history.getCommands());
    }

    @Test
    public void add_null_throwsNullPointerException() {
        history.add("list");

        assertThrows(NullPointerException.class, () -> history.add(null));
        assertEquals(List.of("list"), history.getCommands());
    }

    @Test
    public void add_blankInput_isIgnored() {
        history.add("");
        history.add("   ");
        history.add("\t\r\n");
        history.add("\u2003");

        assertEquals(List.of(), history.getCommands());
    }

    @Test
    public void add_commands_preservesTextAndSubmissionOrder() {
        history.add("  list  ");
        history.add("find /tag Resident");
        history.add("edit 1 /room B-204");

        assertEquals(List.of("  list  ", "find /tag Resident", "edit 1 /room B-204"), history.getCommands());
    }

    @Test
    public void add_repeatedCommand_keepsSeparateEntries() {
        history.add("list");
        history.add("list");

        assertEquals(List.of("list", "list"), history.getCommands());
    }

    @Test
    public void add_invalidCommand_isStoredWithoutValidation() {
        history.add("find /name");
        history.add("unknowncommand");

        assertEquals(List.of("find /name", "unknowncommand"), history.getCommands());
    }

    @Test
    public void add_atCapacity_keepsAllEntries() {
        List<String> submissions = createSubmissions(1, 100);
        submissions.forEach(history::add);

        assertEquals(submissions, history.getCommands());
    }

    @Test
    public void add_beyondCapacity_keepsLatestHundredEntries() {
        createSubmissions(1, 101).forEach(history::add);
        assertEquals(createSubmissions(2, 101), history.getCommands());

        createSubmissions(102, 150).forEach(history::add);
        assertEquals(createSubmissions(51, 150), history.getCommands());
    }

    @Test
    public void add_blankInputAtCapacity_doesNotEvictEntries() {
        List<String> submissions = createSubmissions(1, 100);
        submissions.forEach(history::add);

        history.add(" \t ");

        assertEquals(submissions, history.getCommands());
    }

    @Test
    public void getCommands_modification_throwsUnsupportedOperationException() {
        history.add("list");

        assertThrows(UnsupportedOperationException.class, () -> history.getCommands().add("clear"));
        assertEquals(List.of("list"), history.getCommands());
    }

    @Test
    public void getCommands_laterSubmission_doesNotChangeSnapshot() {
        history.add("list");
        List<String> snapshot = history.getCommands();

        history.add("help");

        assertEquals(List.of("list"), snapshot);
        assertEquals(List.of("list", "help"), history.getCommands());
    }

    private static List<String> createSubmissions(int first, int last) {
        return IntStream.rangeClosed(first, last)
                .mapToObj(number -> "command " + number)
                .toList();
    }
}
