package valueObjects;

import exception.InvalidAccountNumberException;
import exception.InvalidBranchException;
import model.valueobject.AccountIdentity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountIdentityTest {

    private static final AccountIdentity accountIdentity =
            new AccountIdentity(
                    "01",
                    "123456-1"
            );

    @Test
    void shouldCreateValidIdentity() {

        AccountIdentity identity =
                accountIdentity;

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
                () -> createIdentity(
                        "1",
                        "123456-1"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenDigitIsInvalid() {

        assertThrows(
                InvalidAccountNumberException.class,
                () -> createIdentity(
                        "01",
                        "123456-0"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenBranchContainsLetters() {

        assertThrows(
                InvalidBranchException.class,
                () -> createIdentity(
                        "AA",
                        "123456-1"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenBranchIsNull() {

        assertThrows(
                InvalidBranchException.class,
                () -> createIdentity(
                        null,
                        "123456-1"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenBranchIsEmpty() {

        assertThrows(
                InvalidBranchException.class,
                () -> createIdentity(
                        "",
                        "123456-1"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenBranchContainsSpaces() {

        assertThrows(
                InvalidBranchException.class,
                () -> createIdentity(
                        "  ",
                        "123456-1"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenAccountNumberHasWrongFormat() {

        assertThrows(
                InvalidAccountNumberException.class,
                () -> createIdentity(
                        "01",
                        "1234561"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenAccountNumberIsNull() {

        assertThrows(
                InvalidAccountNumberException.class,
                () -> createIdentity(
                        "01",
                        null
                )
        );
    }


    @Test
    void shouldThrowExceptionWhenAccountNumberIsInvalid() {

        assertThrows(
                InvalidAccountNumberException.class,
                () -> createIdentity(
                        "01",
                        "123"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenAccountNumberContainsLetters() {

        assertThrows(
                InvalidAccountNumberException.class,
                () -> createIdentity(
                        "01",
                        "abcdeF-1"
                )
        );
    }

    @Test
    void shouldAllowLeadingZeros() {

        AccountIdentity identity =
                createIdentity(
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

        assertEquals(accountIdentity, accountIdentity);
    }

    @Test
    void shouldHaveSameHashCodeWhenValuesAreEqual() {

        assertEquals(
                accountIdentity.hashCode(),
                accountIdentity.hashCode()
        );
    }

    private AccountIdentity createIdentity(String branch, String accountNumber){
        return new AccountIdentity(branch, accountNumber);
    }
}