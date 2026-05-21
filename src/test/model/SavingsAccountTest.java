package test.model;

import exception.InsufficientBalanceException;
import model.CheckingAccount;
import model.SavingsAccount;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SavingsAccountTest {
    @Test
    void shouldApplyInterestAfterOneMonth() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                createSavingsAccount(january);

        account.deposit(
                Money.of("1000")
        );

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        boolean applied =
                account.applyInterest(february);

        assertTrue(applied);

        assertEquals(
                Money.of("1005"),
                account.getBalance()
        );
    }

    @Test
    void shouldNotApplyInterestBeforeOneMonth() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        Clock interestBeforeOneMonth =
                Clock.fixed(
                        Instant.parse("2026-01-15T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                createSavingsAccount(january);

        boolean applied =
                account.applyInterest(interestBeforeOneMonth);

        assertFalse(applied);
    }

    @Test
    void shouldNotAllowNegativeBalance() {

        SavingsAccount account =
                createSavingsAccount(Clock.systemUTC());

        assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(
                        Money.of("1")
                )
        );
    }

    @Test
    void shouldNotApplyInterestTwiceInSameMonth() {

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        Clock march =
                Clock.fixed(
                        Instant.parse("2026-03-03T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                createSavingsAccount(february);

        account.deposit(
                Money.of("1000")
        );

        boolean first =
                account.applyInterest(march);

        boolean second =
                account.applyInterest(march);

        assertTrue(first);

        assertFalse(second);
    }

    private AccountIdentity createIdentity() {
        return new AccountIdentity(
                "01",
                "123456-1"
        );
    }

    private SavingsAccount createSavingsAccount(Clock clock) {
        return new SavingsAccount(
                UUID.randomUUID(),
                createIdentity(),
                clock
        );
    }
}
