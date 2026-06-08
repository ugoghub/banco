package model;

import exception.InvalidAmountException;
import exception.InvalidTransactionException;
import model.valueobject.AccountIdentity;
import model.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    private static final AccountIdentity accountIdentity =
            new AccountIdentity(
                    "01",
                    "123456-1"
            );

    // =========================
    // Deposit
    // =========================

    @Test
    void shouldCreateDepositTransaction() {

        Clock clock =
                Clock.systemUTC();

        Transaction transaction =
                Transaction.deposit(
                        accountIdentity,
                        Money.of("100"),
                        clock
                );

        assertEquals(
                TransactionType.DEPOSIT,
                transaction.getType()
        );

        assertNull(
                transaction.getSourceIdentity()
        );

        assertEquals(
                accountIdentity,
                transaction.getDestinationIdentity()
        );
    }

    @Test
    void shouldGenerateUniqueIds() {

        Transaction first =
                Transaction.deposit(
                        accountIdentity,
                        Money.of("10"),
                        Clock.systemUTC()
                );

        Transaction second =
                Transaction.deposit(
                        accountIdentity,
                        Money.of("10"),
                        Clock.systemUTC()
                );

        assertNotEquals(
                first.getId(),
                second.getId()
        );
    }

    @Test
    void shouldUseInjectedClock() {

        Clock fixed =
                Clock.fixed(
                        Instant.parse("2026-01-10T10:00:00Z"),
                        ZoneOffset.UTC
                );

        Transaction transaction =
                Transaction.deposit(
                        accountIdentity,
                        Money.of("100"),
                        fixed
                );

        assertEquals(
                LocalDateTime.of(
                        2026,
                        1,
                        10,
                        10,
                        0
                ),
                transaction.getDateTime()
        );
    }

    @Test
    void shouldStoreTransactionAmount() {

        Transaction transaction =
                Transaction.deposit(
                        accountIdentity,
                        Money.of("250.50"),
                        Clock.systemUTC()
                );

        assertEquals(
                Money.of("250.50"),
                transaction.getAmount()
        );
    }

    @Test
    void depositShouldNotAllowDestinationAccountNull() {

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.deposit(
                        null,
                        Money.of("100"),
                        Clock.systemUTC()
                )
        );
    }

    @Test
    void shouldNotAllowDepositNullAmount() {

        assertThrows(
                InvalidAmountException.class,
                () -> Transaction.deposit(
                        accountIdentity,
                        null,
                        Clock.systemUTC()
                )
        );
    }

    @Test
    void shouldNotAllowZeroDepositTransactionAmount() {

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.deposit(
                        accountIdentity,
                        Money.ZERO,
                        Clock.systemUTC()
                )
        );
    }

    @Test
    void shouldNotAllowNegativeDepositTransactionAmount() {

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.deposit(
                        accountIdentity,
                        Money.of("-100"),
                        Clock.systemUTC()
                )
        );
    }

    // =========================
    // Withdraw
    // =========================

    @Test
    void shouldCreateWithdrawTransaction() {

        Transaction transaction =
                Transaction.withdraw(
                        accountIdentity,
                        Money.of("50"),
                        Clock.systemUTC()
                );

        assertEquals(
                TransactionType.WITHDRAW,
                transaction.getType()
        );

        assertEquals(
                accountIdentity,
                transaction.getSourceIdentity()
        );

        assertNull(
                transaction.getDestinationIdentity()
        );
    }

    @Test
    void withdrawShouldNotAllowSourceAccountNull() {

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.withdraw(
                        null,
                        Money.of("100"),
                        Clock.systemUTC()
                )
        );
    }

    @Test
    void shouldNotAllowWithdrawNullAmount() {

        assertThrows(
                InvalidAmountException.class,
                () -> Transaction.withdraw(
                        accountIdentity,
                        null,
                        Clock.systemUTC()
                )
        );
    }

    @Test
    void shouldNotAllowZeroWithdrawTransactionAmount() {

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.withdraw(
                        accountIdentity,
                        Money.ZERO,
                        Clock.systemUTC()
                )
        );
    }

    @Test
    void shouldNotAllowNegativeWithdrawTransactionAmount() {

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.withdraw(
                        accountIdentity,
                        Money.of("-100"),
                        Clock.systemUTC()
                )
        );
    }

    // =========================
    // Transfer
    // =========================

    @Test
    void shouldCreateTransferSentTransaction() {

        AccountIdentity to =
                new AccountIdentity(
                        "01",
                        "000001-1"
                );

        UUID operationId = UUID.randomUUID();

        Transaction transaction =
                Transaction.transferSent(
                        operationId,
                        accountIdentity,
                        to,
                        Money.of("100"),
                        Clock.systemUTC()
                );

        assertEquals(
                TransactionType.TRANSFER_SENT,
                transaction.getType()
        );

        assertEquals(
                accountIdentity,
                transaction.getSourceIdentity()
        );

        assertEquals(
                to,
                transaction.getDestinationIdentity()
        );
    }

    @Test
    void shouldGenerateDifferentIdsForTransferPair() {

        UUID operationId = UUID.randomUUID();

        Transaction sent =
                Transaction.transferSent(
                        operationId,
                        accountIdentity,
                        new AccountIdentity("01", "000001-1"),
                        Money.of("100"),
                        Clock.systemUTC()
                );

        Transaction received =
                Transaction.transferReceived(
                        operationId,
                        accountIdentity,
                        new AccountIdentity("01", "000001-1"),
                        Money.of("100"),
                        Clock.systemUTC()
                );

        assertNotEquals(
                sent.getId(),
                received.getId()
        );

        assertEquals(
                sent.getOperationId(),
                received.getOperationId()
        );
    }

    @Test
    void transferSentShouldNotAllowNullAccounts() {

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.transferSent(
                        UUID.randomUUID(),
                        null,
                        accountIdentity,
                        Money.of("100"),
                        Clock.systemUTC()
                )
        );

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.transferSent(
                        UUID.randomUUID(),
                        accountIdentity,
                        null,
                        Money.of("100"),
                        Clock.systemUTC()
                )
        );
    }

    @Test
    void shouldCreateTransferReceivedTransaction() {

        AccountIdentity from =
                new AccountIdentity(
                        "01",
                        "123456-1"
                );

        AccountIdentity to =
                new AccountIdentity(
                        "01",
                        "000001-1"
                );

        UUID operationId = UUID.randomUUID();

        Transaction transaction =
                Transaction.transferReceived(
                        operationId,
                        from,
                        to,
                        Money.of("100"),
                        Clock.systemUTC()
                );

        assertEquals(
                TransactionType.TRANSFER_RECEIVED,
                transaction.getType()
        );

        assertEquals(
                from,
                transaction.getSourceIdentity()
        );

        assertEquals(
                to,
                transaction.getDestinationIdentity()
        );

        assertEquals(
                operationId,
                transaction.getOperationId()
        );
    }
}
