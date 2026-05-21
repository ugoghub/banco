package test.valueObjects;

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


}
