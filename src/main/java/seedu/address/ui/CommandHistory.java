package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the latest 100 non-blank command submissions for one application session.
 * Commands are kept as submitted, including repeated or invalid commands.
 */
public class CommandHistory {
    private static final int MAX_HISTORY_SIZE = 100;

    private final List<String> commands = new ArrayList<>();

    /**
     * Adds a command submission, ignoring blank input and discarding the oldest entry if the history is full.
     *
     * @throws NullPointerException if {@code commandText} is null.
     */
    public void add(String commandText) {
        requireNonNull(commandText);
        if (commandText.isBlank()) {
            return;
        }

        commands.add(commandText);
        if (commands.size() > MAX_HISTORY_SIZE) {
            commands.removeFirst();
        }
    }

    /**
     * Returns an unmodifiable snapshot of the stored commands, from oldest to newest.
     */
    public List<String> getCommands() {
        return List.copyOf(commands);
    }
}
