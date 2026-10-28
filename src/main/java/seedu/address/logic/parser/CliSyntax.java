package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_PHONE_COMMAND = new Prefix("/phone", true);
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_ADDRESS = new Prefix("a/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_NAME_COMMAND = new Prefix("/name", true);
    public static final Prefix PREFIX_TAG_COMMAND = new Prefix("/tag", true);
    public static final Prefix PREFIX_ROOM_COMMAND = new Prefix("/room", true);
    public static final Prefix PREFIX_NOTE_COMMAND = new Prefix("/note", true);

}
