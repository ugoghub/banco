package test.valueObjects;

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
    void shouldThrowExceptionWhenBranchIsNull() {

        assertThrows(
                InvalidBranchException.class,
                () -> new AccountIdentity(
                        null,
                        "123456-1"
                )
        );
    }
}