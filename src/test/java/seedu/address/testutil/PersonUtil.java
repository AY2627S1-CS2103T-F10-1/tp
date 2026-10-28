package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROOM_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;

import java.util.stream.Collectors;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.PersonField;
import seedu.address.model.person.Person;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code person}, including their room, tags and note
     * if they have any.
     */
    public static String getAddCommand(Person person) {
        String command = AddCommand.COMMAND_WORD + " " + person.getName().fullName
                + " " + PREFIX_PHONE_COMMAND + " " + person.getPhone().value;
        String roomValue = person.getRoom().value;
        String room = roomValue.isEmpty() ? "" : " " + PREFIX_ROOM_COMMAND + " " + roomValue;
        String noteValue = person.getNote().value;
        String note = noteValue.isEmpty() ? "" : " " + PREFIX_NOTE_COMMAND + " " + noteValue;
        return command + room + person.getTags().stream()
                .map(tag -> " " + PREFIX_TAG_COMMAND + " " + tag.tagName)
                .collect(Collectors.joining()) + note;
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     */
    public static String getPersonDetails(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + person.getName().fullName + " ");
        sb.append(PREFIX_PHONE + person.getPhone().value + " ");
        sb.append(PREFIX_EMAIL + person.getEmail().value + " ");
        sb.append(PREFIX_ADDRESS + person.getAddress().value + " ");
        person.getTags().stream().forEach(
            s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditPersonDescriptor}'s details.
     */
    public static String getEditPersonDescriptorDetails(EditPersonDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.get(PersonField.NAME).ifPresent(name ->
                sb.append(PREFIX_NAME_COMMAND).append(" ").append(name.fullName).append(" "));
        descriptor.get(PersonField.PHONE).ifPresent(phone ->
                sb.append(PREFIX_PHONE_COMMAND).append(" ").append(phone.value).append(" "));
        descriptor.get(PersonField.ROOM).ifPresent(room ->
                sb.append(PREFIX_ROOM_COMMAND).append(" ").append(room.value).append(" "));
        descriptor.get(PersonField.TAGS).ifPresent(tags -> {
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAG_COMMAND).append(" ");
            }
            tags.forEach(tag -> sb.append(PREFIX_TAG_COMMAND).append(" ").append(tag.tagName).append(" "));
        });
        descriptor.get(PersonField.NOTE).ifPresent(note ->
                sb.append(PREFIX_NOTE_COMMAND).append(" ").append(note.value).append(" "));
        return sb.toString();
    }
}
