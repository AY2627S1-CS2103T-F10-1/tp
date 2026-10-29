package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NoteTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Note(null));
    }

    @Test
    public void constructor_normalizesWhitespace_success() {
        assertEquals("Allergic to peanuts", new Note("  Allergic   to\tpeanuts ").value);
        assertEquals("", new Note("   ").value);
    }

    @Test
    public void constructor_invalidNote_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Note.MESSAGE_CONSTRAINTS, () -> new Note("see /docs"));
        assertThrows(IllegalArgumentException.class, Note.MESSAGE_CONSTRAINTS, () ->
                new Note("a".repeat(Note.MAX_LENGTH + 1)));
    }

    @Test
    public void isValidNote_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Note.isValidNote(null));
    }

    @Test
    public void isValidNote_length() {
        assertTrue(Note.isValidNote(""));
        assertTrue(Note.isValidNote("a"));
        assertTrue(Note.isValidNote("a".repeat(Note.MAX_LENGTH)));
        assertFalse(Note.isValidNote("a".repeat(Note.MAX_LENGTH + 1)));
    }

    @Test
    public void isValidNote_characters() {
        assertTrue(Note.isValidNote("Allergic to peanuts! Call mum: +65 9123-4567 (after 6pm), ok?"));
        assertTrue(Note.isValidNote("Uses a wheelchair; room is near the lift & stairs"));
    }

    @Test
    public void isValidNote_slashInsideWord_true() {
        assertTrue(Note.isValidNote("call w/ parent"));
        assertTrue(Note.isValidNote("Call 24/7 if unwell, mum and/or dad"));
        assertTrue(Note.isValidNote("Discharged 12/10, see https://nus.edu.sg"));
        assertTrue(Note.isValidNote("ends with/"));
    }

    @Test
    public void isValidNote_wordStartingWithSlash_false() {
        assertFalse(Note.isValidNote("/note"));
        assertFalse(Note.isValidNote("ends with /"));
        assertFalse(Note.isValidNote("see /docs first"));
        assertFalse(Note.isValidNote("tab\t/docs"));
        assertFalse(Note.isValidNote("//comment"));
    }

    @Test
    public void equals_hashCode_toString() {
        Note note = new Note("Prefers WhatsApp");
        assertEquals(note, new Note("Prefers   WhatsApp"));
        assertEquals(note.hashCode(), new Note("Prefers WhatsApp").hashCode());
        assertFalse(note.equals(new Note("Prefers calls")));
        assertFalse(note.equals(null));
        assertFalse(note.equals("Prefers WhatsApp"));
        assertEquals("Prefers WhatsApp", note.toString());
    }
}
