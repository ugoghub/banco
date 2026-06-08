package model;

import exception.InsufficientBalanceException;
import helper.AccountFactory;
import model.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckingAccountTest {

    private final Clock clock =
            Clock.systemUTC();

    // =========================
    // OverdraftLimit
    // =========================

    @Test
    void shouldAllowWithdrawExactlyAtOverdraftLimit() {

        CheckingAccount account =
                AccountFactory.checking(clock);

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
                AccountFactory.checking(clock);

        assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(
                        Money.of("2000")
                )
        );
    }

    @Test
    void shouldNotExceedOverdraftLimitAfterMultipleWithdraws() {

        CheckingAccount account =
                AccountFactory.checking(clock);

        account.withdraw(Money.of("200"));
        account.withdraw(Money.of("200"));
        account.withdraw(Money.of("200"));

        assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(
                        Money.of("500")
                )
        );
    }

    @Test
    void shouldReduceNegativeBalanceAfterDeposit() {

        CheckingAccount account =
                AccountFactory.checking(clock);

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
                AccountFactory.checking(clock);

        account.withdraw(Money.of("500"));

        account.deposit(Money.of("700"));

        assertEquals(
                Money.of("200"),
                account.getBalance()
        );
    }

    @Test
    void shouldAllowWithdrawWhenBalancePlusOverdraftIsEnough() {

        CheckingAccount account =
                AccountFactory.checking(clock);

        account.deposit(
                Money.of("200")
        );

        account.withdraw(
                Money.of("1000")
        );

        assertEquals(
                Money.of("-800"),
                account.getBalance()
        );
    }
}
