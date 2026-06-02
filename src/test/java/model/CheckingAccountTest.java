package model;

import exception.InsufficientBalanceException;
import exception.InvalidAmountException;
import model.valueobject.AccountIdentity;
import model.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CheckingAccountTest {

    private static final AccountIdentity accountIdentity =
            new AccountIdentity(
                    "01",
                    "123456-1"
            );

    private final Clock clock =
            Clock.systemUTC();

    @Test
    void shouldStartWithZeroBalance() {

        CheckingAccount account =
                createCheckingAccount(clock);

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

        CheckingAccount account =
                createCheckingAccount(fixedClock);

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

    @Test
    void shouldAllowRemovalWhenBalanceIsZero() {

        CheckingAccount account =
                createCheckingAccount(clock);

        assertTrue(account.canBeRemoved());
    }

    @Test
    void shouldNotAllowRemovalWhenBalanceIsNotZero() {

        CheckingAccount account =
                createCheckingAccount(clock);

        account.deposit(Money.of("1"));

        assertFalse(account.canBeRemoved());
    }

    // =========================
    // Deposit
    // =========================

    @Test
    void shouldDepositMoney() {

        CheckingAccount account =
                createCheckingAccount(clock);

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

        CheckingAccount account =
                createCheckingAccount(clock);

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
                createCheckingAccount(clock);

        assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(Money.ZERO)
        );
    }

    // =========================
    // Withdraw
    // =========================

    @Test
    void shouldAllowOverdraftUntilLimit() {

        CheckingAccount account =
                createCheckingAccount(clock);

        account.withdraw(
                Money.of("500")
        );

        assertEquals(
                Money.of("-500.00"),
                account.getBalance()
        );
    }

    @Test
    void shouldAllowWithdrawExactlyAtOverdraftLimit() {

        CheckingAccount account =
                createCheckingAccount(clock);

        account.withdraw(
                Money.of("1000")
        );

        assertEquals(
                Money.of("-1000.00"),
                account.getBalance()
        );
    }

    @Test
    void shouldNotExceedOverdraftLimit() {

        CheckingAccount account =
                createCheckingAccount(clock);

        assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(
                        Money.of("2000")
                )
        );
    }

    @Test
    void shouldNotAllowZeroWithdraw() {

        CheckingAccount account =
                createCheckingAccount(clock);

        assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(
                        Money.ZERO
                )
        );
    }

    @Test
    void shouldNotExceedOverdraftLimitAfterMultipleWithdraws() {

        CheckingAccount account =
                createCheckingAccount(clock);

        account.withdraw(Money.of("600"));

        assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(
                        Money.of("500")
                )
        );
    }

    @Test
    void shouldNotAllowNegativeWithdraw() {

        CheckingAccount account =
                createCheckingAccount(clock);

        assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(
                        Money.of("-1")
                )
        );
    }

    // =========================
    // Withdraw e Deposit
    // =========================

    @Test
    void shouldWithdrawMoney() {

        CheckingAccount account =
                createCheckingAccount(clock);

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



    @Test
    void shouldReduceNegativeBalanceAfterDeposit() {

        CheckingAccount account =
                createCheckingAccount(clock);

        account.withdraw(
                Money.of("500")
        );

        account.deposit(
                Money.of("200")
        );

        assertEquals(
                Money.of("-300"),
                account.getBalance()
        );
    }

    @Test
    void shouldRecoverFromNegativeBalance() {

        CheckingAccount account =
                createCheckingAccount(clock);

        account.withdraw(Money.of("500"));

        account.deposit(Money.of("700"));

        assertEquals(
                Money.of("200"),
                account.getBalance()
        );
    }

    private CheckingAccount createCheckingAccount(Clock clock) {
        return new CheckingAccount(
                UUID.randomUUID(),
                accountIdentity,
                clock
        );
    }
}
