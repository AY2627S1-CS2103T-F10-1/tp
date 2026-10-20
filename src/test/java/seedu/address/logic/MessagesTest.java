package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

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
}
