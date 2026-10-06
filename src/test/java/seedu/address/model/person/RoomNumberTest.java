package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RoomNumberTest {
    @Test
    public void constructor_validValue_normalizesWhitespace() {
        assertEquals("Block B-204", new RoomNumber("  Block   B-204  ").value);
        assertEquals("1204", new RoomNumber("1204").toString());
    }

    @Test
    public void constructor_invalidValue_throwsException() {
        assertThrows(NullPointerException.class, () -> new RoomNumber(null));
        assertThrows(IllegalArgumentException.class, () -> new RoomNumber(""));
        assertThrows(IllegalArgumentException.class, () -> new RoomNumber("B/204"));
        assertThrows(IllegalArgumentException.class, () -> new RoomNumber("123456789012345678901"));
    }

    @Test
    public void isValidRoomNumber_checksCharactersAndLength() {
        assertFalse(RoomNumber.isValidRoomNumber(null));
        assertFalse(RoomNumber.isValidRoomNumber(" "));
        assertFalse(RoomNumber.isValidRoomNumber("B_204"));
        assertTrue(RoomNumber.isValidRoomNumber("B-204"));
        assertTrue(RoomNumber.isValidRoomNumber("12345678901234567890"));
    }

    @Test
    public void equals_comparesValues() {
        RoomNumber room = new RoomNumber("B-204");
        assertTrue(room.equals(room));
        assertTrue(room.equals(new RoomNumber("B-204")));
        assertEquals(room.hashCode(), new RoomNumber("B-204").hashCode());
        assertFalse(room.equals(null));
        assertFalse(room.equals("B-204"));
        assertFalse(room.equals(new RoomNumber("B-205")));
    }
}
