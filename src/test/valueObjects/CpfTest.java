package test.valueObjects;

import exception.InvalidCpfException;
import model.valueObjects.Cpf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpfTest {

    @Test
    void shouldCreateValidCpf() {

        Cpf cpf = new Cpf("52998224725");

        assertEquals("52998224725", cpf.value());
    }

    @Test
    void shouldThrowExceptionWhenCpfIsInvalid() {

        assertThrows(
                InvalidCpfException.class,
                () -> new Cpf("123")
        );
    }

    @Test
    void shouldRemoveCpfFormatting() {

        Cpf cpf = new Cpf("529.982.247-25");

        assertEquals("52998224725", cpf.value());
    }
}