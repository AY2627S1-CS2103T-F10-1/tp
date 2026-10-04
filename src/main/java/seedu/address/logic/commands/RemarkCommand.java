package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.logic.commands.exceptions.CommandException;

/** Adds or replaces a person's remark. */
public class RemarkCommand extends Command {
    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": edits a person's remark. Parameters: INDEX "
            + PREFIX_REMARK + "REMARK";
    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to Person: %1$s";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from Person: %1$s";

    private final Index index;
    private final Remark remark;

    public RemarkCommand(Index index, Remark remark) {
        requireAllNonNull(index, remark);
        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Person> persons = model.getFilteredPersonList();
        if (index.getZeroBased() >= persons.size()) throw new CommandException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        Person old = persons.get(index.getZeroBased());
        Person updated = new Person(old.getName(), old.getPhone(), old.getEmail(), old.getAddress(), remark, old.getTags());
        model.setPerson(old, updated);
        model.updateFilteredPersonList(x -> true);
        String message = remark.value.isEmpty() ? MESSAGE_DELETE_REMARK_SUCCESS : MESSAGE_ADD_REMARK_SUCCESS;
        return new CommandResult(String.format(message, updated));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof RemarkCommand
                && index.equals(((RemarkCommand) other).index) && remark.equals(((RemarkCommand) other).remark));
    }
}
