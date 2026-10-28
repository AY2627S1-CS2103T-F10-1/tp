package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ToggleCommand;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

public class ToggleCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ToggleCommand.MESSAGE_USAGE);

    private final ToggleCommandParser parser = new ToggleCommandParser();

    @Test
    public void parse_validArgs_success() {
        assertParseSuccess(parser, " 1 /tag friends", new ToggleCommand(INDEX_FIRST_PERSON, tags("friends")));

        // multiple tags, with extra whitespace and tags containing spaces
        assertParseSuccess(parser, "  1   /tag  friends   /tag Floor  3",
                new ToggleCommand(INDEX_FIRST_PERSON, tags("friends", "Floor 3")));
    }

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " /tag friends", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        assertParseFailure(parser, " 0 /tag friends", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " -1 /tag friends", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 random /tag friends", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unknownPrefix_failure() {
        assertParseFailure(parser, " 1 /tag friends /phone 91234567", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /Tag friends", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidTag_failure() {
        assertParseFailure(parser, " 1 /tag hubby*", Tag.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, " 1 /tag", Tag.MESSAGE_EMPTY);
        assertParseFailure(parser, " 1 /tag friends /tag", Tag.MESSAGE_EMPTY);
    }

    @Test
    public void parse_duplicateTag_failure() {
        String expected = String.format(ToggleCommandParser.MESSAGE_REPEATED_TAG, "Friends");
        assertParseFailure(parser, " 1 /tag friends /tag Friends", expected);
        assertParseFailure(parser, " 1 /tag Friends /tag  friends ", expected);
    }

    @Test
    public void parse_tooManyTags_failure() {
        assertParseFailure(parser, " 1 /tag a /tag b /tag c /tag d /tag e /tag f", Person.MESSAGE_TAG_LIMIT_REACHED);
    }

    private static Set<Tag> tags(String... names) {
        Set<Tag> tags = new LinkedHashSet<>();
        for (String name : names) {
            tags.add(new Tag(name));
        }
        return tags;
    }
}
