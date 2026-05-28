package valueObjects;

import exception.InvalidAccountNumberException;
import exception.InvalidBranchException;
import model.valueObjects.AccountIdentity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountIdentityTest {

    @Test
    void shouldCreateValidIdentity() {

        AccountIdentity identity =
                new AccountIdentity(
                        "01",
                        "123456-1"
                );

        assertEquals("01", identity.branch());

        assertEquals(
                "123456-1",
                identity.accountNumber()
        );
    }

    @Test
    void shouldThrowExceptionWhenBranchIsInvalid() {

        assertThrows(
                InvalidBranchException.class,
                () -> new AccountIdentity(
                        "1",
                        "123456-1"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenDigitIsInvalid() {

        assertThrows(
                InvalidAccountNumberException.class,
                () -> new AccountIdentity(
                        "01",
                        "123456-0"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenBranchContainsLetters() {

        assertThrows(
                InvalidBranchException.class,
                () -> new AccountIdentity(
                        "AA",
                        "123456-1"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenBranchIsNull() {

        assertThrows(
                InvalidBranchException.class,
                () -> new AccountIdentity(
                        null,
                        "123456-1"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenBranchIsEmpty() {

        assertThrows(
                InvalidBranchException.class,
                () -> new AccountIdentity(
                        "",
                        "123456-1"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenBranchContainsSpaces() {

        assertThrows(
                InvalidBranchException.class,
                () -> new AccountIdentity(
                        "  ",
                        "123456-1"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenAccountNumberHasWrongFormat() {

        assertThrows(
                InvalidAccountNumberException.class,
                () -> new AccountIdentity(
                        "01",
                        "1234561"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenAccountNumberIsNull() {

        assertThrows(
                InvalidAccountNumberException.class,
                () -> new AccountIdentity(
                        "01",
                        null
                )
        );
    }


    @Test
    void shouldThrowExceptionWhenAccountNumberIsInvalid() {

        assertThrows(
                InvalidAccountNumberException.class,
                () -> new AccountIdentity(
                        "01",
                        "123"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenAccountNumberContainsLetters() {

        assertThrows(
                InvalidAccountNumberException.class,
                () -> new AccountIdentity(
                        "01",
                        "abcdeF-1"
                )
        );
    }

    @Test
    void shouldAllowLeadingZeros() {

        AccountIdentity identity =
                new AccountIdentity(
                        "00",
                        "000001-1"
                );

        assertEquals("00", identity.branch());

        assertEquals(
                "000001-1",
                identity.accountNumber()
        );
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {

        AccountIdentity first =
                new AccountIdentity(
                        "01",
                        "123456-1"
                );

        AccountIdentity second =
                new AccountIdentity(
                        "01",
                        "123456-1"
                );

        assertEquals(first, second);
    }

    @Test
    void shouldHaveSameHashCodeWhenValuesAreEqual() {

        AccountIdentity first =
                new AccountIdentity(
                        "01",
                        "123456-1"
                );

        AccountIdentity second =
                new AccountIdentity(
                        "01",
                        "123456-1"
                );

        assertEquals(
                first.hashCode(),
                second.hashCode()
        );
    }
}