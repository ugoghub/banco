package model;

import exception.InvalidTransactionException;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    void shouldCreateDepositTransaction() {

        AccountIdentity account =
                new AccountIdentity(
                        "01",
                        "123456-1"
                );

        Clock clock =
                Clock.systemUTC();

        Transaction transaction =
                Transaction.deposit(
                        account,
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
                account,
                transaction.getDestinationIdentity()
        );
    }

    @Test
    void shouldCreateWithdrawTransaction() {

        AccountIdentity account =
                new AccountIdentity(
                        "01",
                        "123456-1"
                );

        Transaction transaction =
                Transaction.withdraw(
                        account,
                        Money.of("50"),
                        Clock.systemUTC()
                );

        assertEquals(
                TransactionType.WITHDRAW,
                transaction.getType()
        );

        assertEquals(
                account,
                transaction.getSourceIdentity()
        );

        assertNull(
                transaction.getDestinationIdentity()
        );
    }

    @Test
    void shouldCreateTransferSentTransaction() {

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
                Transaction.transferSent(
                        operationId,
                        from,
                        to,
                        Money.of("100"),
                        Clock.systemUTC()
                );

        assertEquals(
                TransactionType.TRANSFER_SENT,
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
    }

    @Test
    void shouldGenerateUniqueIds() {

        AccountIdentity account =
                new AccountIdentity(
                        "01",
                        "123456-1"
                );

        Transaction first =
                Transaction.deposit(
                        account,
                        Money.of("10"),
                        Clock.systemUTC()
                );

        Transaction second =
                Transaction.deposit(
                        account,
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
                        new AccountIdentity(
                                "01",
                                "123456-1"
                        ),
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
    void shouldKeepSameOperationIdForTransfer() {

        UUID operationId = UUID.randomUUID();

        Transaction sent =
                Transaction.transferSent(
                        operationId,
                        new AccountIdentity("01", "123456-1"),
                        new AccountIdentity("01", "000001-1"),
                        Money.of("100"),
                        Clock.systemUTC()
                );

        assertEquals(
                operationId,
                sent.getOperationId()
        );
    }

    @Test
    void shouldStoreTransactionAmount() {

        Transaction transaction =
                Transaction.deposit(
                        new AccountIdentity("01", "123456-1"),
                        Money.of("250.50"),
                        Clock.systemUTC()
                );

        assertEquals(
                Money.of("250.50"),
                transaction.getAmount()
        );
    }

    @Test
    void shouldGenerateDifferentIdsForTransferPair() {

        UUID operationId = UUID.randomUUID();

        Transaction sent =
                Transaction.transferSent(
                        operationId,
                        new AccountIdentity("01", "123456-1"),
                        new AccountIdentity("01", "000001-1"),
                        Money.of("100"),
                        Clock.systemUTC()
                );

        Transaction received =
                Transaction.transferReceived(
                        operationId,
                        new AccountIdentity("01", "123456-1"),
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
    void transferSentShouldNotAllowNullAccounts() {

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.transferSent(
                        UUID.randomUUID(),
                        null,
                        new AccountIdentity("01", "123456-1"),
                        Money.of("100"),
                        Clock.systemUTC()
                )
        );

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.transferSent(
                        UUID.randomUUID(),
                        new AccountIdentity("01", "123456-1"),
                        null,
                        Money.of("100"),
                        Clock.systemUTC()
                )
        );
    }

    @Test
    void shouldNotAllowNegativeTransactionAmount() {

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.deposit(
                        new AccountIdentity("01", "123456-1"),
                        Money.of("-100"),
                        Clock.systemUTC()
                )
        );
    }

    @Test
    void shouldNotAllowZeroTransactionAmount() {

        assertThrows(
                InvalidTransactionException.class,
                () -> Transaction.withdraw(
                        new AccountIdentity("01", "123456-1"),
                        Money.ZERO,
                        Clock.systemUTC()
                )
        );
    }

    @Test
    void shouldNotAllowNullAmount() {

        assertThrows(
                NullPointerException.class,
                () -> Transaction.deposit(
                        new AccountIdentity("01", "123456-1"),
                        null,
                        Clock.systemUTC()
                )
        );
    }
}
