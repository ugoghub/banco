package valueObjects;

import exception.InvalidPersonNameException;
import model.valueobject.PersonName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonNameTest {

    // =========================
    // General
    // =========================

    @Test
    void shouldCreateValidName() {

        PersonName name =
                createPersonName("Hugo Silva");

        assertEquals(
                "Hugo Silva",
                name.value()
        );
    }

    @Test
    void shouldTrimName() {

        PersonName name =
                createPersonName("   Hugo Silva   ");

        assertEquals(
                "Hugo Silva",
                name.value()
        );
    }

    @Test
    void shouldNormalizeMultipleInternalSpaces() {

        PersonName name =
                createPersonName(
                        "Hugo    Silva"
                );

        assertEquals(
                "Hugo Silva",
                name.value()
        );
    }

    @Test
    void shouldAllowCompoundNames() {

        PersonName name =
                createPersonName(
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
                createPersonName(
                        "João da Silva"
                );

        assertEquals(
                "João da Silva",
                name.value()
        );
    }

    // =========================
    // Validation
    // =========================

    @Test
    void shouldThrowExceptionWhenNameIsInvalid() {

        assertThrows(
                InvalidPersonNameException.class,
                () -> createPersonName("A")
        );
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {

        assertThrows(
                InvalidPersonNameException.class,
                () -> createPersonName(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenNameContainsOnlySpaces() {

        assertThrows(
                InvalidPersonNameException.class,
                () -> createPersonName("     ")
        );
    }

    // =========================
    // Equality
    // =========================

    @Test
    void shouldBeEqualAfterNormalization() {

        PersonName first =
                createPersonName(" Hugo   Silva ");

        PersonName second =
                createPersonName("Hugo Silva");

        assertEquals(first, second);
    }

    @Test
    void shouldHaveSameHashCodeWhenValuesAreEqual() {

        PersonName first =
                createPersonName(" Hugo   Silva ");

        PersonName second =
                createPersonName("Hugo Silva");

        assertEquals(first.hashCode(), second.hashCode());
    }

    // =========================
    // Helper
    // =========================

    private PersonName createPersonName(String name){
        return new PersonName(name);
    }
}
