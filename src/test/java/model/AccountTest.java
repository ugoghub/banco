package model;

import exception.InvalidAccountIdentityException;
import exception.InvalidAmountException;
import exception.InvalidClientIdException;
import exception.InvalidClockException;
import helper.AccountFactory;
import model.valueobject.AccountIdentity;
import model.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    private final Clock clock =
            Clock.systemUTC();

    // =========================
    // General
    // =========================

    @Test
    void shouldStartWithZeroBalance() {

        CheckingAccount account =
                AccountFactory.checking(clock);

        assertEquals(
                Money.ZERO,
                account.getBalance()
        );
    }

    @Test
    void shouldRegisterCreationTime() {

        Clock fixedClock =
                Clock.fixed(
                        Instant.parse("2026-01-10T10:00:00Z"),
                        ZoneOffset.UTC
                );

        SavingsAccount account =
                AccountFactory.savings(fixedClock);

        assertEquals(
                LocalDateTime.of(
                        2026,
                        1,
                        10,
                        10,
                        0
                ),
                account.getCreationTime()
        );
    }

    // =========================
    // Validation
    // =========================

    @Test
    void shouldThrowExceptionWhenCreatingAccountWithNullClientId() {

        assertThrows(
                InvalidClientIdException.class,
                () -> new CheckingAccount(
                        null,
                        new AccountIdentity("01", "123456-1"),
                        Clock.systemUTC()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingAccountWithNullAccountIdentity() {

        assertThrows(
                InvalidAccountIdentityException.class,
                () -> new CheckingAccount(
                        UUID.randomUUID(),
                        null,
                        Clock.systemUTC()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingAccountWithNullClock() {

        assertThrows(
                InvalidClockException.class,
                () -> new CheckingAccount(
                        UUID.randomUUID(),
                        new AccountIdentity("01", "123456-1"),
                        null
                )
        );
    }


    // =========================
    // Delete
    // =========================

    @Test
    void shouldAllowRemovalWhenBalanceIsZero() {

        CheckingAccount account =
                AccountFactory.checking(clock);

        assertTrue(account.canBeRemoved());
    }

    @Test
    void shouldNotAllowRemovalWhenBalanceIsNotZero() {

        SavingsAccount account =
                AccountFactory.savings(clock);

        account.deposit(Money.of("1"));

        assertFalse(account.canBeRemoved());
    }

    @Test
    void shouldAllowRemovingAccountAfterReturningToZeroBalance() {

        CheckingAccount account =
                AccountFactory.checking(clock);

        account.deposit(
                Money.of("100")
        );

        account.withdraw(
                Money.of("100")
        );

        assertTrue(
                account.canBeRemoved()
        );
    }

    // =========================
    // Deposit
    // =========================

    @Test
    void shouldDepositMoney() {

        CheckingAccount account =
                AccountFactory.checking(clock);

        account.deposit(
                Money.of("100")
        );

        assertEquals(
                Money.of("100.00"),
                account.getBalance()
        );
    }

    @Test
    void shouldNotAllowNegativeDeposit() {

        SavingsAccount account =
                AccountFactory.savings(clock);

        assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(
                        Money.of("-10")
                )
        );
    }

    @Test
    void shouldNotAllowZeroDeposit() {

        CheckingAccount account =
                AccountFactory.checking(clock);

        assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(Money.ZERO)
        );
    }

    // =========================
    // Withdraw
    // =========================

    @Test
    void shouldNotAllowZeroWithdraw() {

        SavingsAccount account =
                AccountFactory.savings(clock);

        assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(
                        Money.ZERO
                )
        );
    }

    @Test
    void shouldNotAllowNegativeWithdraw() {

        CheckingAccount account =
                AccountFactory.checking(clock);

        assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(
                        Money.of("-1")
                )
        );
    }

    // =========================
    // Balance
    // =========================

    @Test
    void shouldWithdrawMoney() {

        SavingsAccount account =
                AccountFactory.savings(clock);

        account.deposit(
                Money.of("100")
        );

        account.withdraw(
                Money.of("40")
        );

        assertEquals(
                Money.of("60.00"),
                account.getBalance()
        );
    }
}
