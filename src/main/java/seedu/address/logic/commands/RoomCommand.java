package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.RoomNumber;

/** Assigns or replaces a room number for a resident. */
public class RoomCommand extends Command {
    public static final String COMMAND_WORD = "room";
    public static final String MESSAGE_USAGE = "Usage: <resident identifier> /room <room number>";
    public static final String MESSAGE_SUCCESS = "Room number added to %s.";
    public static final String MESSAGE_NOT_FOUND = "Contact not found.";
    public static final String MESSAGE_NOT_RESIDENT = "Room number can only be assigned to a resident.";

    private final String identifier;
    private final RoomNumber roomNumber;

    /**
     * Creates a command to assign {@code roomNumber} to the resident with the given name.
     */
    public RoomCommand(String identifier, RoomNumber roomNumber) {
        this.identifier = identifier;
        this.roomNumber = roomNumber;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> matches = model.getFilteredPersonList().stream()
                .filter(person -> person.getName().fullName.equalsIgnoreCase(identifier))
                .toList();
        if (matches.isEmpty()) {
            throw new CommandException(MESSAGE_NOT_FOUND);
        }
        Person person = matches.get(0);
        if (!person.isResident()) {
            throw new CommandException(MESSAGE_NOT_RESIDENT);
        }
        Person updatedPerson = new Person(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(),
                person.getTags(), java.util.Optional.of(roomNumber));
        model.setPerson(person, updatedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, person.getName().fullName));
    }
}
