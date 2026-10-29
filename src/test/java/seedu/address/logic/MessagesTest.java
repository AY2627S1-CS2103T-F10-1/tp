package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Messages.format(null));
    }

    @Test
    public void format_personWithoutRoom_omitsRoom() {
        Person person = new PersonBuilder().withName("Amy Bee").withPhone("85355255").withTags("Friend").build();

        assertEquals("Amy Bee; Phone: 85355255; Tags: [Friend]", Messages.format(person));
    }

    @Test
    public void format_personWithRoom_showsRoomBeforeTags() {
        Person person = new PersonBuilder().withName("Amy Bee").withPhone("85355255").withRoom("B-204")
                .withTags("Friend", "Resident Fellow").build();

        assertEquals("Amy Bee; Phone: 85355255; Room: B-204; Tags: [Friend][Resident Fellow]",
                Messages.format(person));
    }

    @Test
    public void format_personWithoutNote_omitsNote() {
        assertEquals("Alice Pauline; Phone: 94351253; Tags: [Friends]", Messages.format(ALICE));
    }

    @Test
    public void format_personWithNote_showsNote() {
        assertEquals("Alice Pauline; Phone: 94351253; Tags: [Friends]; Note: Allergic to peanuts",
                Messages.format(new PersonBuilder(ALICE).withNote("Allergic to peanuts").build()));
    }

    @Test
    public void format_personWithRoomAndNote_showsNoteAfterTags() {
        Person person = new PersonBuilder(ALICE).withRoom("B-204").withNote("Allergic to peanuts").build();

        assertEquals("Alice Pauline; Phone: 94351253; Room: B-204; Tags: [Friends]; Note: Allergic to peanuts",
                Messages.format(person));
    }
}
