package model;

import exception.InsufficientBalanceException;
import helper.AccountFactory;
import model.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SavingsAccountTest {

    // =========================
    // Interest
    // =========================
    @Test
    void shouldApplyInterestAfterOneMonth() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                AccountFactory.savings(january);

        account.deposit(
                Money.of("1000")
        );

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        List<Money> appliedInterests =
                account.applyPendingInterests(february);

        assertEquals(1, appliedInterests.size());

        assertEquals(
                Money.of("5.00"),
                appliedInterests.getFirst()
        );

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

        Clock beforeOneMonth =
                Clock.fixed(
                        Instant.parse("2026-01-15T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                AccountFactory.savings(january);

        List<Money> appliedInterests =
                account.applyPendingInterests(beforeOneMonth);

        assertTrue(appliedInterests.isEmpty());
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
                AccountFactory.savings(february);

        account.deposit(
                Money.of("1000")
        );

        List<Money> first =
                account.applyPendingInterests(march);

        List<Money> second =
                account.applyPendingInterests(march);

        assertEquals(1, first.size());

        assertTrue(second.isEmpty());
    }

    @Test
    void shouldApplyInterestAgainInFutureMonths() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                AccountFactory.savings(january);

        account.deposit(Money.of("1000"));

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        List<Money> applied =
                account.applyPendingInterests(february);

        assertEquals(1, applied.size());

        Clock march =
                Clock.fixed(
                        Instant.parse("2026-03-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        applied =
                account.applyPendingInterests(march);

        assertEquals(1, applied.size());

        assertEquals(
                Money.of("1010.02"),
                account.getBalance()
        );
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
                AccountFactory.savings(january);

        List<Money> appliedInterests =
                account.applyPendingInterests(february);

        assertTrue(appliedInterests.isEmpty());

        assertEquals(
                Money.ZERO,
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
                AccountFactory.savings(january);

        account.deposit(
                Money.of("1000")
        );

        List<Money> appliedInterests =
                account.applyPendingInterests(july);

        assertEquals(6, appliedInterests.size());

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
                AccountFactory.savings(january15);

        account.deposit(Money.of("1000"));

        Clock february14 =
                Clock.fixed(
                        Instant.parse("2026-02-14T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        List<Money> before =
                account.applyPendingInterests(february14);

        assertTrue(before.isEmpty());

        Clock february15 =
                Clock.fixed(
                        Instant.parse("2026-02-15T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        List<Money> after =
                account.applyPendingInterests(february15);

        assertEquals(1, after.size());
    }

    @Test
    void shouldNotAccumulateRetroactiveInterestWhileBalanceWasZero() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                AccountFactory.savings(january);

        Clock april =
                Clock.fixed(
                        Instant.parse("2026-04-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        account.applyPendingInterests(april);

        account.deposit(Money.of("1000"));

        List<Money> applied =
                account.applyPendingInterests(april);

        assertTrue(applied.isEmpty());

        assertEquals(
                Money.of("1000"),
                account.getBalance()
        );
    }

    @Test
    void shouldRoundInterestCorrectlyForSmallAmounts() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        SavingsAccount account =
                AccountFactory.savings(january);

        account.deposit(Money.of("0.01"));

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneId.systemDefault()
                );

        List<Money> applied =
                account.applyPendingInterests(february);

        assertEquals(1, applied.size());

        assertEquals(
                Money.of("0.00"),
                applied.getFirst()
        );

        assertEquals(
                Money.of("0.01"),
                account.getBalance()
        );
    }

    // =========================
    // Withdraw
    // =========================

    @Test
    void shouldNotAllowNegativeBalance() {

        SavingsAccount account =
                AccountFactory.savings(Clock.systemUTC());

        assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(
                        Money.of("1")
                )
        );
    }
}