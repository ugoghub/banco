package test.model;

import exception.InsufficientBalanceException;
import exception.InvalidAmountException;
import model.Account;
import model.CheckingAccount;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckingAccountTest {

    private final Clock clock =
            Clock.systemUTC();

    @Test
    void shouldDepositMoney() {

        CheckingAccount account =
                createCheckingAccount();

        account.deposit(
                Money.of("100")
        );

        assertEquals(
                Money.of("100.00"),
                account.getBalance()
        );
    }

    @Test
    void shouldIncreaseBalanceAfterDeposit() {

        Account account =
                new CheckingAccount(
                        UUID.randomUUID(),
                        new AccountIdentity("01", "123456-1"),
                        Clock.systemUTC()
                );

        account.deposit(Money.of("100"));

        assertEquals(
                Money.of("100"),
                account.getBalance()
        );
    }

    @Test
    void shouldAllowOverdraftUntilLimit() {

        CheckingAccount account =
                createCheckingAccount();

        account.withdraw(
                Money.of("500")
        );

        assertEquals(
                Money.of("-500.00"),
                account.getBalance()
        );
    }

    @Test
    void shouldStartWithZeroBalance() {

        CheckingAccount account =
                new CheckingAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        Clock.systemUTC()
                );

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
                new CheckingAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        fixedClock
                );

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
    void shouldAllowWithdrawExactlyAtOverdraftLimit() {

        CheckingAccount account =
                new CheckingAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        clock
                );

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
                createCheckingAccount();

        assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(
                        Money.of("2000")
                )
        );
    }

    @Test
    void shouldWithdrawMoney() {

        CheckingAccount account =
                createCheckingAccount();

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
                new CheckingAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        clock
                );

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
    void shouldKeepBalanceExactlyAtNegativeLimit() {

        CheckingAccount account =
                new CheckingAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        clock
                );

        account.withdraw(
                Money.of("1000")
        );

        assertEquals(
                Money.of("-1000"),
                account.getBalance()
        );
    }

    @Test
    void shouldNotAllowNegativeDeposit() {

        CheckingAccount account =
                createCheckingAccount();

        assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(
                        Money.of("-10")
                )
        );
    }

    @Test
    void shouldNotAllowZeroWithdraw() {

        CheckingAccount account =
                createCheckingAccount();

        assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(
                        Money.ZERO
                )
        );
    }

    private CheckingAccount createCheckingAccount() {
        return new CheckingAccount(
                UUID.randomUUID(),
                createIdentity(),
                clock
        );
    }

    private AccountIdentity createIdentity() {
        return new AccountIdentity(
                "01",
                "123456-1"
        );
    }
}
