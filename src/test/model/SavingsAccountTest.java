package test.model;

import exception.InsufficientBalanceException;
import model.SavingsAccount;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
                new SavingsAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        january
                );

        account.deposit(
                new Money(new BigDecimal("1000"))
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
                new BigDecimal("1005.00"),
                account.getBalance().value()
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
                new SavingsAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        january
                );

        boolean applied =
                account.applyInterest(interestBeforeOneMonth);

        assertFalse(applied);
    }

    private AccountIdentity createIdentity() {
        return new AccountIdentity(
                "01",
                "123456-1"
        );
    }

    @Test
    void shouldNotAllowNegativeBalance() {

        SavingsAccount account =
                new SavingsAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        Clock.systemUTC()
                );

        assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(
                        new Money(new BigDecimal("1"))
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
                new SavingsAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        february
                );

        account.deposit(
                new Money(new BigDecimal("1000"))
        );

        boolean first =
                account.applyInterest(march);

        boolean second =
                account.applyInterest(march);

        assertTrue(first);

        assertFalse(second);
    }
}
