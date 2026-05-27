package valueObjects;

import exception.InvalidEmailException;
import model.valueObjects.Email;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {

        Email email =
                new Email("teste@gmail.com");

        assertEquals(
                "teste@gmail.com",
                email.value()
        );
    }

    @Test
    void shouldNormalizeEmail() {

        Email email =
                new Email("  TESTE@GMAIL.COM ");

        assertEquals(
                "teste@gmail.com",
                email.value()
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {

        assertThrows(
                InvalidEmailException.class,
                () -> new Email("teste.com")
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {

        assertThrows(
                InvalidEmailException.class,
                () -> new Email(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {

        assertThrows(
                InvalidEmailException.class,
                () -> new Email("")
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailHasNoDomain() {

        assertThrows(
                InvalidEmailException.class,
                () -> new Email("teste@")
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailHasNoUser() {

        assertThrows(
                InvalidEmailException.class,
                () -> new Email("@gmail.com")
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailHasMultipleAtSymbols() {

        assertThrows(
                InvalidEmailException.class,
                () -> new Email("teste@@gmail.com")
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailDomainIsInvalid() {

        assertThrows(
                InvalidEmailException.class,
                () -> new Email("teste@gmail")
        );
    }

    @Test
    void shouldBeEqualAfterNormalization() {

        Email first =
                new Email("TESTE@GMAIL.COM");

        Email second =
                new Email("teste@gmail.com");

        assertEquals(first, second);
    }

    @Test
    void shouldThrowExceptionWhenEmailContainsInternalSpaces() {

        assertThrows(
                InvalidEmailException.class,
                () -> new Email("tes te@gmail.com")
        );
    }
}
