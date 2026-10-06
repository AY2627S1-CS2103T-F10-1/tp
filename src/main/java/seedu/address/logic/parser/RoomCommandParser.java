package seedu.address.logic.parser;

import static seedu.address.logic.commands.RoomCommand.MESSAGE_USAGE;

import seedu.address.logic.commands.RoomCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.RoomNumber;

/** Parses room-number commands. */
public class RoomCommandParser implements Parser<RoomCommand> {
    public RoomCommand parse(String args) throws ParseException {
        int delimiterIndex = args.indexOf("/room");
        if (delimiterIndex <= 0 || args.indexOf("/room", delimiterIndex + 1) >= 0) {
            throw new ParseException(MESSAGE_USAGE);
        }
        String identifier = args.substring(0, delimiterIndex).trim();
        String roomValue = args.substring(delimiterIndex + "/room".length()).trim();
        if (identifier.isEmpty()) {
            throw new ParseException(MESSAGE_USAGE);
        }
        if (roomValue.isEmpty()) {
            throw new ParseException("Room number cannot be empty.");
        }
        if (!RoomNumber.isValidRoomNumber(roomValue)) {
            if (roomValue.length() > 20) {
                throw new ParseException("Room number too long, please shorten it and try again.");
            }
            throw new ParseException("Room number contains invalid characters.");
        }
        return new RoomCommand(identifier, new RoomNumber(roomValue));
    }
}
