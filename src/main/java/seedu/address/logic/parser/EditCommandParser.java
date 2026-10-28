package seedu.address.logic.parser;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditCommand object.
 * Every field in {@link PersonField#editableValues()} can be edited. Tags are changed with {@code toggle} instead.
 */
public class EditCommandParser implements Parser<EditCommand> {
    private static final Prefix[] PREFIXES = PersonField.editableValues().stream()
            .map(PersonField::getPrefix)
            .toArray(Prefix[]::new);
    private static final Prefix[] SINGLE_VALUED_PREFIXES = PersonField.editableValues().stream()
            .filter(field -> !field.isRepeatable())
            .map(PersonField::getPrefix)
            .toArray(Prefix[]::new);

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ParserUtil.parseIndexedArguments(args, EditCommand.MESSAGE_USAGE, PREFIXES);
        Index index = ParserUtil.parseIndex(argMultimap.getPreamble());

        argMultimap.verifyNoDuplicatePrefixesFor(SINGLE_VALUED_PREFIXES);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        for (PersonField<?> field : PersonField.editableValues()) {
            editPersonDescriptor.setIfGiven(field, argMultimap);
        }

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }
}
