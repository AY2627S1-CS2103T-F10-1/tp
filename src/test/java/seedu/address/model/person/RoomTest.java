package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RoomTest {

    @Test
    public void constructor_normalizesWhitespace_success() {
        assertEquals("B-204", new Room("  B-204  ").value);
        assertEquals("B 204", new Room("B   204").value);
    }

    @Test
    public void constructor_invalidRoom_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Room("B#204"));
    }

    @Test
    public void isValidRoom() {
        assertTrue(Room.isValidRoom(""));
        assertTrue(Room.isValidRoom("B-204"));
        assertFalse(Room.isValidRoom("B#204"));
        assertFalse(Room.isValidRoom("B".repeat(Room.MAX_LENGTH + 1)));
    }

    @Test
    public void equals_hashCode_toString() {
        Room room = new Room("B-204");
        assertEquals(room, new Room("B-204"));
        assertEquals(room.hashCode(), new Room("B-204").hashCode());
        assertFalse(room.equals(null));
        assertFalse(room.equals("B-204"));
        assertEquals("B-204", room.toString());
    }
}
