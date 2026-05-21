package test.valueObjects;

import exception.InvalidPersonNameException;
import model.valueObjects.PersonName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonNameTest {

    @Test
    void shouldCreateValidName() {

        PersonName name =
                new PersonName("Hugo Silva");

        assertEquals(
                "Hugo Silva",
                name.value()
        );
    }

    @Test
    void shouldTrimName() {

        PersonName name =
                new PersonName("   Hugo Silva   ");

        assertEquals(
                "Hugo Silva",
                name.value()
        );
    }

    @Test
    void shouldThrowExceptionWhenNameIsInvalid() {

        assertThrows(
                InvalidPersonNameException.class,
                () -> new PersonName("A")
        );
    }
}
