package test.model;

import exception.InsufficientBalanceException;
import exception.InvalidAmountException;
import model.CheckingAccount;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckingAccountTest {

    private final Clock clock =
            Clock.systemDefaultZone();

    @Test
    void shouldDepositMoney() {

        CheckingAccount account =
                new CheckingAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        clock
                );

        account.deposit(
                new Money(new BigDecimal("100"))
        );

        assertEquals(
                new BigDecimal("100.00"),
                account.getBalance().value()
        );
    }

    @Test
    void shouldAllowOverdraftUntilLimit() {

        CheckingAccount account =
                new CheckingAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        clock
                );

        account.withdraw(
                new Money(new BigDecimal("500"))
        );

        assertEquals(
                new BigDecimal("-500.00"),
                account.getBalance().value()
        );
    }

    @Test
    void shouldNotExceedOverdraftLimit() {

        CheckingAccount account =
                new CheckingAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        clock
                );

        assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(
                        new Money(new BigDecimal("2000"))
                )
        );
    }

    private AccountIdentity createIdentity() {
        return new AccountIdentity(
                "01",
                "123456-1"
        );
    }

    @Test
    void shouldWithdrawMoney() {

        CheckingAccount account =
                new CheckingAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        clock
                );

        account.deposit(
                new Money(new BigDecimal("100"))
        );

        account.withdraw(
                new Money(new BigDecimal("40"))
        );

        assertEquals(
                new BigDecimal("60.00"),
                account.getBalance().value()
        );
    }

    @Test
    void shouldNotAllowNegativeDeposit() {

        CheckingAccount account =
                new CheckingAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        clock
                );

        assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(
                        new Money(new BigDecimal("-10"))
                )
        );
    }

    @Test
    void shouldNotAllowZeroWithdraw() {

        CheckingAccount account =
                new CheckingAccount(
                        UUID.randomUUID(),
                        createIdentity(),
                        clock
                );

        assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(
                        Money.ZERO
                )
        );
    }
}
