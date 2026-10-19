package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditCommand object.
 * Every {@link PersonField} can be edited, so a field added to the model is editable as soon as it is registered.
 */
public class EditCommandParser implements Parser<EditCommand> {
    private static final String WORD_SEPARATOR = " ";

    private static final Prefix[] PREFIXES = PersonField.values().stream()
            .map(PersonField::getPrefix)
            .toArray(Prefix[]::new);
    private static final Prefix[] SINGLE_VALUED_PREFIXES = PersonField.values().stream()
            .filter(field -> !field.isRepeatable())
            .map(PersonField::getPrefix)
            .toArray(Prefix[]::new);

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String normalized = WORD_SEPARATOR + StringUtil.collapseWhitespace(args);
        if (ParserUtil.hasUnknownPrefix(normalized, PREFIXES)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalized, PREFIXES);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(SINGLE_VALUED_PREFIXES);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        for (PersonField<?> field : PersonField.values()) {
            editPersonDescriptor.setIfGiven(field, argMultimap);
        }

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }
}
