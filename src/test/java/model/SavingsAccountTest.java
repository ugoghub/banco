package model;

import exception.InsufficientBalanceException;
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

        int appliedPeriods =
                account.applyPendingInterests(february);

        assertEquals(1, appliedPeriods);

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

        Clock stillJanuary =
                Clock.fixed(
                        Instant.parse("2026-01-15T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                createSavingsAccount(january);

        int appliedPeriods =
                account.applyPendingInterests(stillJanuary);

        assertEquals(0, appliedPeriods);
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

        int first =
                account.applyPendingInterests(march);

        int second =
                account.applyPendingInterests(march);

        assertEquals(1, first);

        assertEquals(0, second);
    }

    @Test
    void shouldNotApplyInterestWithZeroBalance() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                createSavingsAccount(january);

        int appliedPeriods =
                account.applyPendingInterests(february);

        assertEquals(1, appliedPeriods);

        assertEquals(
                Money.ZERO,
                account.getBalance()
        );
    }

    @Test
    void shouldApplyCompoundInterestForMultipleMonths() {

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

        Clock april =
                Clock.fixed(
                        Instant.parse("2026-04-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        int appliedPeriods =
                account.applyPendingInterests(april);

        assertEquals(3, appliedPeriods);

        assertEquals(
                Money.of("1015.07"),
                account.getBalance()
        );
    }

    @Test
    void shouldApplyPendingInterestForManyMonthsAtOnce() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        Clock july =
                Clock.fixed(
                        Instant.parse("2026-07-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                createSavingsAccount(january);

        account.deposit(
                Money.of("1000")
        );

        int appliedPeriods =
                account.applyPendingInterests(july);

        assertEquals(6, appliedPeriods);

        assertEquals(
                Money.of("1030.38"),
                account.getBalance()
        );
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