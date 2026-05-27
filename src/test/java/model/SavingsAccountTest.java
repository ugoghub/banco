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

        assertEquals(0, appliedPeriods);

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

    @Test
    void shouldApplyInterestOnlyAfterAccountAnniversaryDay() {

        Clock january15 =
                Clock.fixed(
                        Instant.parse("2026-01-15T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                createSavingsAccount(january15);

        account.deposit(Money.of("1000"));

        Clock february14 =
                Clock.fixed(
                        Instant.parse("2026-02-14T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        int before =
                account.applyPendingInterests(february14);

        assertEquals(0, before);

        Clock february15 =
                Clock.fixed(
                        Instant.parse("2026-02-15T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        int after =
                account.applyPendingInterests(february15);

        assertEquals(1, after);
    }

    @Test
    void shouldNotAccumulateRetroactiveInterestWhileBalanceWasZero() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                createSavingsAccount(january);

        Clock april =
                Clock.fixed(
                        Instant.parse("2026-04-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        account.applyPendingInterests(april);

        account.deposit(Money.of("1000"));

        int applied =
                account.applyPendingInterests(april);

        assertEquals(0, applied);

        assertEquals(
                Money.of("1000"),
                account.getBalance()
        );
    }

    @Test
    void shouldApplyInterestAgainInFutureMonths() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                createSavingsAccount(january);

        account.deposit(Money.of("1000"));

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        int applied = account.applyPendingInterests(february);

        assertEquals(1, applied);

        Clock march =
                Clock.fixed(
                        Instant.parse("2026-03-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        applied =
                account.applyPendingInterests(march);

        assertEquals(1, applied);

        assertEquals(
                Money.of("1010.02"),
                account.getBalance()
        );
    }

    @Test
    void shouldApplyInterestToSmallAmounts() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                createSavingsAccount(january);

        account.deposit(Money.of("0.01"));

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        int applied = account.applyPendingInterests(february);

        assertEquals(1, applied);

        assertEquals(
                Money.of("0.01"),
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