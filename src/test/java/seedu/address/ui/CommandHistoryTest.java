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

    @Test
    public void navigate_emptyHistory_returnsUnchangedInput() {
        assertEquals("unfinished input", history.navigatePrevious("unfinished input"));
        assertEquals("unfinished input", history.navigateNext("unfinished input"));
        assertEquals("", history.navigatePrevious(""));
        assertEquals("", history.navigateNext(""));
    }

    @Test
    public void navigatePrevious_atOldestEntry_staysAtOldestCommand() {
        history.add("list");
        history.add("help");

        assertEquals("help", history.navigatePrevious("draft"));
        assertEquals("list", history.navigatePrevious("help"));
        assertEquals("list", history.navigatePrevious("list"));
        assertEquals("help", history.navigateNext("list"));
        assertEquals("draft", history.navigateNext("help"));
    }

    @Test
    public void navigateNext_withoutBrowsing_returnsUnchangedInput() {
        history.add("list");

        assertEquals("draft", history.navigateNext("draft"));
        assertEquals("edited draft", history.navigateNext("edited draft"));
        assertEquals("list", history.navigatePrevious("edited draft"));
        assertEquals("edited draft", history.navigateNext("list"));
    }

    @Test
    public void navigateNext_pastNewestEntry_restoresExactDraft() {
        history.add("list");
        history.add("help");
        String draft = "  edit 1 /room B-  ";

        assertEquals("help", history.navigatePrevious(draft));
        assertEquals("list", history.navigatePrevious("help"));
        assertEquals("help", history.navigateNext("list"));
        assertEquals(draft, history.navigateNext("help"));
        assertEquals(draft, history.navigateNext(draft));
    }

    @Test
    public void navigateNext_emptyDraft_restoresEmptyInput() {
        history.add("list");

        assertEquals("list", history.navigatePrevious(""));
        assertEquals("", history.navigateNext("list"));
        assertEquals("", history.navigateNext(""));
    }

    @Test
    public void navigate_recalledTextEdited_preservesHistoryAndOriginalDraft() {
        history.add("list");
        history.add("find /tag Resident");

        history.navigatePrevious("unfinished draft");
        assertEquals("list", history.navigatePrevious("find /tag Staff"));
        assertEquals("find /tag Resident", history.navigateNext("edited older command"));
        assertEquals("unfinished draft", history.navigateNext("edited newest command"));
        assertEquals(List.of("list", "find /tag Resident"), history.getCommands());
    }

    @Test
    public void navigate_afterDraftRestored_savesNewDraft() {
        history.add("list");
        history.navigatePrevious("first draft");
        assertEquals("first draft", history.navigateNext("list"));

        assertEquals("list", history.navigatePrevious("second draft"));
        assertEquals("second draft", history.navigateNext("list"));
    }

    @Test
    public void add_whileBrowsing_resetsNavigationAndDiscardsOldDraft() {
        history.add("list");
        history.add("help");
        history.navigatePrevious("old draft");
        history.navigatePrevious("help");

        history.add("find /tag Resident");

        assertEquals("", history.navigateNext(""));
        assertEquals("find /tag Resident", history.navigatePrevious(""));
        assertEquals("", history.navigateNext("find /tag Resident"));
        assertEquals(List.of("list", "help", "find /tag Resident"), history.getCommands());
    }

    @Test
    public void add_recalledCommandAgain_resetsNavigationAndKeepsDuplicate() {
        history.add("list");
        history.navigatePrevious("old draft");

        history.add("list");

        assertEquals("list", history.navigatePrevious("new draft"));
        assertEquals("list", history.navigatePrevious("list"));
        assertEquals("list", history.navigateNext("list"));
        assertEquals("new draft", history.navigateNext("list"));
        assertEquals(List.of("list", "list"), history.getCommands());
    }

    @Test
    public void navigate_afterEviction_usesRetainedEntriesAndResetsOnSubmission() {
        createSubmissions(1, 101).forEach(history::add);
        String currentInput = "draft";
        for (int number = 101; number >= 2; number--) {
            currentInput = history.navigatePrevious(currentInput);
            assertEquals("command " + number, currentInput);
        }
        assertEquals("command 2", history.navigatePrevious(currentInput));

        history.add("command 102");

        assertEquals("command 102", history.navigatePrevious("new draft"));
        assertEquals("new draft", history.navigateNext("command 102"));
        assertEquals(createSubmissions(3, 102), history.getCommands());
    }

    @Test
    public void add_blankInputWhileBrowsing_keepsPositionAndDraft() {
        history.add("list");
        history.add("help");
        history.navigatePrevious("draft");
        history.navigatePrevious("help");

        history.add(" \t ");

        assertEquals("help", history.navigateNext("list"));
        assertEquals("draft", history.navigateNext("help"));
        assertEquals(List.of("list", "help"), history.getCommands());
    }

    @Test
    public void navigate_nullInput_throwsWithoutChangingNavigation() {
        history.add("list");

        assertThrows(NullPointerException.class, () -> history.navigatePrevious(null));
        assertEquals("list", history.navigatePrevious("draft"));
        assertThrows(NullPointerException.class, () -> history.navigateNext(null));
        assertEquals("draft", history.navigateNext("list"));
    }

    private static List<String> createSubmissions(int first, int last) {
        return IntStream.rangeClosed(first, last)
                .mapToObj(number -> "command " + number)
                .toList();
    }
}
