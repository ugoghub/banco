package valueObjects;

import exception.InvalidEmailException;
import model.valueobject.Cpf;
import model.valueobject.Email;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {

        Email email =
                createEmail("teste@gmail.com");

        assertEquals(
                "teste@gmail.com",
                email.value()
        );
    }

    @Test
    void shouldNormalizeEmail() {

        Email email =
                createEmail("  TESTE@GMAIL.COM ");

        assertEquals(
                "teste@gmail.com",
                email.value()
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {

        assertThrows(
                InvalidEmailException.class,
                () -> createEmail("teste.com")
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {

        assertThrows(
                InvalidEmailException.class,
                () -> createEmail(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {

        assertThrows(
                InvalidEmailException.class,
                () -> createEmail("")
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailHasNoDomain() {

        assertThrows(
                InvalidEmailException.class,
                () -> createEmail("teste@")
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailHasNoUser() {

        assertThrows(
                InvalidEmailException.class,
                () -> createEmail("@gmail.com")
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailHasMultipleAtSymbols() {

        assertThrows(
                InvalidEmailException.class,
                () -> createEmail("teste@@gmail.com")
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailDomainIsInvalid() {

        assertThrows(
                InvalidEmailException.class,
                () -> createEmail("teste@gmail")
        );
    }

    @Test
    void shouldBeEqualAfterNormalization() {

        Email first =
                createEmail("TESTE@GMAIL.COM");

        Email second =
                createEmail("teste@gmail.com");

        assertEquals(first, second);
    }

    @Test
    void shouldThrowExceptionWhenEmailContainsInternalSpaces() {

        assertThrows(
                InvalidEmailException.class,
                () -> createEmail("tes te@gmail.com")
        );
    }

    private Email createEmail(String email){
        return new Email(email);
    }
}
