package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class RoomEqualsPredicateTest {
    @Test
    public void test_exactRoom_ignoresCaseAndWhitespace() {
        Person person = new PersonBuilder().withRoom("B 204").build();
        assertTrue(new RoomEqualsPredicate(" b   204 \t").test(person));
        assertFalse(new RoomEqualsPredicate("B").test(person));
        assertFalse(new RoomEqualsPredicate("B204").test(person));
        assertFalse(new RoomEqualsPredicate("B-204").test(person));
    }

    @Test
    public void test_searchesOnlyRoomAndDoesNotMatchMissingRooms() {
        Person person = new PersonBuilder().withName("B204").withRoom("C-101").build();
        assertFalse(new RoomEqualsPredicate("B204").test(person));
        assertFalse(new RoomEqualsPredicate("B#204").test(person));
        assertFalse(new RoomEqualsPredicate("C-101").test(new PersonBuilder().build()));
    }

    @Test
    public void equals_normalizedValuesHaveSameHashCode() {
        RoomEqualsPredicate predicate = new RoomEqualsPredicate("B 204");
        RoomEqualsPredicate equivalent = new RoomEqualsPredicate(" b   204 ");
        assertEquals(predicate, equivalent);
        assertEquals(predicate.hashCode(), equivalent.hashCode());
        assertFalse(predicate.equals(new RoomEqualsPredicate("C-101")));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("B 204"));
    }

    @Test
    public void constructor_nullRoom_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RoomEqualsPredicate(null));
    }

    @Test
    public void toStringMethod() {
        assertEquals(RoomEqualsPredicate.class.getCanonicalName() + "{room=b-204}",
                new RoomEqualsPredicate("B-204").toString());
    }
}
