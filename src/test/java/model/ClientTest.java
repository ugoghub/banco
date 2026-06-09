package model;

import exception.InvalidCpfException;
import exception.InvalidEmailException;
import exception.InvalidPersonNameException;
import model.valueobject.Cpf;
import model.valueobject.Email;
import model.valueobject.PersonName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClientTest {

    @Test
    void shouldThrowExceptionWhenCreatingClientWithNullName() {

        assertThrows(
                InvalidPersonNameException.class,
                () -> new Client(null, new Cpf("52998224725"), new Email("pedro@gmail.com"))
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingClientWithNullCpf() {

        assertThrows(
                InvalidCpfException.class,
                () -> new Client(new PersonName("pedro"), null, new Email("pedro@gmail.com"))
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingClientWithNullEmail() {

        assertThrows(
                InvalidEmailException.class,
                () -> new Client(new PersonName("pedro"), new Cpf("52998224725"), null)
        );
    }
}
