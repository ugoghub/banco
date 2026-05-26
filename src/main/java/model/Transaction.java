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
    private final UUID operationId;
    private final TransactionType type;
    private final Money amount;
    private final AccountIdentity sourceIdentity;
    private final AccountIdentity destinationIdentity;
    private final LocalDateTime dateTime;

    private Transaction(UUID operationId,
                        TransactionType type,
                        Money amount,
                        AccountIdentity sourceIdentity,
                        AccountIdentity destinationIdentity, Clock clock) {

        Objects.requireNonNull(type);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(clock); //ver questão de NullException

        this.id = UUID.randomUUID();
        this.operationId = operationId;
        this.type = type;
        this.amount = amount;
        this.dateTime = LocalDateTime.now(clock);
        this.sourceIdentity = sourceIdentity;
        this.destinationIdentity = destinationIdentity;

        validateState();
    }

    private void validateState() {
        //validação defensiva

        switch (type) {

            case DEPOSIT -> validateDeposit();

            case WITHDRAW -> validateWithdraw();

            case TRANSFER_SENT, TRANSFER_RECEIVED -> validateTransfer();
        }
    }


    // =========================
    // Getters
    // =========================
    public UUID getId() {
        return id;
    }

    public UUID getOperationId() {
        return operationId;
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

    // =========================
    // Helpers
    // =========================

    private void validateDeposit() {
        if (sourceIdentity != null || destinationIdentity == null) {
            throw new InvalidTransactionException("DEPÓSITO não deve possuir conta de origem");
        }
    }

    private void validateWithdraw() {
        if (sourceIdentity == null || destinationIdentity != null) {
            throw new InvalidTransactionException("SAQUE não deve possuir conta de destino");
        }
    }

    private void validateTransfer() {
        if (operationId == null){
            throw new InvalidTransactionException("Toda transferência deve possuir um ID de operação");
        }
        if (sourceIdentity == null || destinationIdentity == null) {
            throw new InvalidTransactionException("Transferência não deve possuir origem e/ou destino nulls");
        }
    }

    // =========================
    // Factory Methods
    // =========================

    public static Transaction deposit(AccountIdentity accountIdentity,
                                      Money amount,
                                      Clock clock) {
        return new Transaction(null, TransactionType.DEPOSIT, amount, null, accountIdentity, clock);
    }

    public static Transaction withdraw(AccountIdentity accountIdentity,
                                       Money amount,
                                       Clock clock) {
        return new Transaction(null, TransactionType.WITHDRAW, amount, accountIdentity, null, clock);
    }

    public static Transaction transferSent(UUID operationId,
                                           AccountIdentity from,
                                           AccountIdentity to,
                                           Money amount,
                                           Clock clock) {
        return new Transaction(operationId, TransactionType.TRANSFER_SENT, amount, from, to, clock);
    }

    public static Transaction transferReceived(UUID operationId,
                                               AccountIdentity from,
                                               AccountIdentity to,
                                               Money amount,
                                               Clock clock) {
        return new Transaction(operationId, TransactionType.TRANSFER_RECEIVED, amount, from, to, clock);
    }
}