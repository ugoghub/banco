package model;

import exception.InvalidTransactionException;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final TransactionType type;
    private final Money amount;
    private final AccountIdentity sourceIdentity;
    private final AccountIdentity destinationIdentity;
    private final LocalDateTime dateTime;

    private Transaction(TransactionType type,
                       Money amount,
                       AccountIdentity sourceIdentity,
                       AccountIdentity destinationIdentity, Clock clock) {

        Objects.requireNonNull(type);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(clock); //ver questão de NullException

        this.id = UUID.randomUUID();
        this.type = type;
        this.amount = amount;
        this.dateTime = LocalDateTime.now(clock);
        this.sourceIdentity = sourceIdentity;
        this.destinationIdentity = destinationIdentity;

        validateState();
    }

    public static Transaction deposit(AccountIdentity accountIdentity,
                                      Money amount,
                                      Clock clock) {
        return new Transaction(TransactionType.DEPOSIT, amount, null, accountIdentity, clock);
    }

    public static Transaction withdraw(AccountIdentity accountIdentity,
                                      Money amount,
                                      Clock clock) {
        return new Transaction(TransactionType.WITHDRAW, amount, accountIdentity, null, clock);
    }

    public static Transaction transferSent(AccountIdentity from,
                                   AccountIdentity to,
                                   Money amount,
                                   Clock clock) {
        return new Transaction(TransactionType.TRANSFER_SENT, amount, from, to, clock);
    }

    public static Transaction transferReceived(AccountIdentity from,
                                   AccountIdentity to,
                                   Money amount,
                                   Clock clock) {
        return new Transaction(TransactionType.TRANSFER_RECEIVED, amount, from, to, clock);
    }

    public UUID getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public AccountIdentity getSourceIdentity() {
        return sourceIdentity;
    }

    public AccountIdentity getDestinationIdentity() {
        return destinationIdentity;
    }

    private void validateState() {

        switch (type) {

            case DEPOSIT -> {
                if (sourceIdentity != null || destinationIdentity == null) {
                    throw new InvalidTransactionException("Transação inválida");
                }
            }

            case WITHDRAW -> {
                if (sourceIdentity == null || destinationIdentity != null) {
                    throw new InvalidTransactionException("Transação inválida");
                }
            }

            case TRANSFER_SENT, TRANSFER_RECEIVED -> {
                if (sourceIdentity == null || destinationIdentity == null) {
                    throw new InvalidTransactionException("Transação inválida");
                }
            }
        }
    }
}