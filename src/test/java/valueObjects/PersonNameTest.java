package valueObjects;

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

    @Test
    void shouldThrowExceptionWhenNameIsNull() {

        assertThrows(
                InvalidPersonNameException.class,
                () -> new PersonName(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {

        assertThrows(
                InvalidPersonNameException.class,
                () -> new PersonName("")
        );
    }

    @Test
    void shouldThrowExceptionWhenNameContainsOnlySpaces() {

        assertThrows(
                InvalidPersonNameException.class,
                () -> new PersonName("     ")
        );
    }

    @Test
    void shouldNormalizeMultipleInternalSpaces() {

        PersonName name =
                new PersonName(
                        "Hugo    Silva"
                );

        assertEquals(
                "Hugo Silva",
                name.value()
        );
    }

    @Test
    void shouldBeEqualAfterNormalization() {

        PersonName first =
                new PersonName(" Hugo   Silva ");

        PersonName second =
                new PersonName("Hugo Silva");

        assertEquals(first, second);
    }

    @Test
    void shouldAllowCompoundNames() {

        PersonName name =
                new PersonName(
                        "Maria Clara Souza"
                );

        assertEquals(
                "Maria Clara Souza",
                name.value()
        );
    }

    @Test
    void shouldAllowAccentedCharacters() {

        PersonName name =
                new PersonName(
                        "João da Silva"
                );

        assertEquals(
                "João da Silva",
                name.value()
        );
    }
}
