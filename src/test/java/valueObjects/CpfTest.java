package valueObjects;

import exception.InvalidCpfException;
import model.valueobject.Cpf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CpfTest {

    @Test
    void shouldCreateValidCpf() {

        Cpf cpf = createCpf("52998224725");

        assertEquals("52998224725", cpf.value());
    }

    @Test
    void shouldThrowExceptionWhenCpfIsInvalid() {

        assertThrows(
                InvalidCpfException.class,
                () -> createCpf("123")
        );
    }

    @Test
    void shouldRemoveCpfFormatting() {

        Cpf cpf = createCpf("529.982.247-25");

        assertEquals("52998224725", cpf.value());
    }

    @Test
    void shouldThrowExceptionWhenCpfIsNull() {

        assertThrows(
                InvalidCpfException.class,
                () -> createCpf(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenCpfContainsLetters() {

        assertThrows(
                InvalidCpfException.class,
                () -> createCpf("52998A24725")
        );
    }

    @Test
    void shouldThrowExceptionWhenCpfHasAllEqualDigits() {

        assertThrows(
                InvalidCpfException.class,
                () -> createCpf("11111111111")
        );
    }

    @Test
    void shouldThrowExceptionWhenCpfCheckDigitsAreInvalid() {

        assertThrows(
                InvalidCpfException.class,
                () -> createCpf("52998224724")
        );
    }

    @Test
    void shouldTrimCpfBeforeValidation() {

        Cpf cpf =
                createCpf(" 529.982.247-25 ");

        assertEquals(
                "52998224725",
                cpf.value()
        );
    }

    @Test
    void shouldBeEqualWhenCpfValuesAreEqual() {

        Cpf first =
                createCpf("52998224725");

        Cpf second =
                createCpf("529.982.247-25");

        assertEquals(first, second);
    }

    private Cpf createCpf(String cpf){
        return new Cpf(cpf);
    }
}