package test.model;

import exception.InsufficientBalanceException;
import exception.InvalidAmountException;
import model.Account;
import model.CheckingAccount;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;
import org.junit.jupiter.api.Test;

import java.time.Clock;
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
