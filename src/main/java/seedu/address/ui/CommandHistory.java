package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.util.StringUtil;

/**
 * Stores and browses the latest 100 non-blank command submissions for one application session.
 * Commands are kept as submitted, including repeated or invalid commands.
 */
public class CommandHistory {
    private static final int MAX_HISTORY_SIZE = 100;

    private final List<String> commands = new ArrayList<>();

    /** Index of the displayed entry, or {@code commands.size()} when not browsing history. */
    private int historyPosition;
    private String draft = "";

    /**
     * Adds a command submission and resets navigation, discarding the oldest entry if the history is full.
     * Blank input is ignored without changing the history or navigation.
     *
     * @throws NullPointerException if {@code commandText} is null.
     */
    public void add(String commandText) {
        requireNonNull(commandText);
        if (StringUtil.isBlank(commandText)) {
            return;
        }

        commands.add(commandText);
        if (commands.size() > MAX_HISTORY_SIZE) {
            commands.removeFirst();
        }
        historyPosition = commands.size();
        draft = "";
    }

    /**
     * Moves to an older command, saving {@code currentInput} as the draft when browsing begins.
     * Returns the oldest command when already at that entry, or the unchanged input if history is empty.
     * Edits to recalled text do not change stored commands or the saved draft.
     *
     * @throws NullPointerException if {@code currentInput} is null.
     */
    public String navigatePrevious(String currentInput) {
        requireNonNull(currentInput);
        if (commands.isEmpty()) {
            return currentInput;
        }

        if (historyPosition == commands.size()) {
            draft = currentInput;
        }
        if (historyPosition > 0) {
            historyPosition--;
        }
        return commands.get(historyPosition);
    }

    /**
     * Moves to a newer command, restoring the saved draft after the newest entry.
     * Returns {@code currentInput} unchanged when not browsing history.
     *
     * @throws NullPointerException if {@code currentInput} is null.
     */
    public String navigateNext(String currentInput) {
        requireNonNull(currentInput);
        if (historyPosition == commands.size()) {
            return currentInput;
        }

        historyPosition++;
        if (historyPosition == commands.size()) {
            return draft;
        }
        return commands.get(historyPosition);
    }

    /**
     * Returns an unmodifiable snapshot of the stored commands, from oldest to newest.
     */
    public List<String> getCommands() {
        return List.copyOf(commands);
    }
}
