package model;

import exception.InvalidTransactionException;
import model.valueobject.AccountIdentity;
import model.valueobject.Money;

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
                        AccountIdentity destinationIdentity,
                        Clock clock) {

        validateTransactionState(operationId, type, sourceIdentity, destinationIdentity);

        validateAmount(amount);

        if(clock == null) {
            throw new InvalidTransactionException("Horário inválido");
        }

        this.id = UUID.randomUUID();
        this.operationId = operationId;
        this.sourceIdentity = sourceIdentity;
        this.destinationIdentity = destinationIdentity;
        this.type = type;
        this.amount = amount;
        this.dateTime = LocalDateTime.now(clock);
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

    public static Transaction interest(AccountIdentity accountIdentity, Money amount, Clock clock) {
        return new Transaction(null, TransactionType.INTEREST, amount, null, accountIdentity, clock);
    }

    // =========================
    // Validation
    // =========================

    private static void validateTransactionState(UUID operationId,
                                                 TransactionType type,
                                                 AccountIdentity sourceIdentity,
                                                 AccountIdentity destinationIdentity) {
        //validação defensiva

        if(type == null){
            throw new InvalidTransactionException(
                    "Tipo de transação inválido"
            );
        }

        switch (type) {

            case DEPOSIT -> validateDeposit(sourceIdentity, destinationIdentity);

            case WITHDRAW -> validateWithdraw(sourceIdentity, destinationIdentity);

            case TRANSFER_SENT, TRANSFER_RECEIVED -> validateTransfer(operationId, sourceIdentity, destinationIdentity);

            case INTEREST -> validateInterest(sourceIdentity, destinationIdentity);
        }
    }

    private static void validateDeposit(AccountIdentity sourceIdentity,
                                        AccountIdentity destinationIdentity) {
        if (sourceIdentity != null || destinationIdentity == null) {
            throw new InvalidTransactionException("DEPÓSITO não deve possuir conta de origem");
        }
    }

    private static void validateWithdraw(AccountIdentity sourceIdentity,
                                         AccountIdentity destinationIdentity) {
        if (sourceIdentity == null || destinationIdentity != null) {
            throw new InvalidTransactionException("SAQUE não deve possuir conta de destino");
        }
    }

    private static void validateTransfer(UUID operationId,
                                         AccountIdentity sourceIdentity,
                                         AccountIdentity destinationIdentity) {
        if (operationId == null){
            throw new InvalidTransactionException("Toda transferência deve possuir um ID de operação");
        }
        if (sourceIdentity == null || destinationIdentity == null) {
            throw new InvalidTransactionException("Transferência não deve possuir origem e/ou destino nulls");
        }
    }

    private static void validateInterest(AccountIdentity sourceIdentity,
                                         AccountIdentity destinationIdentity) {

        if (sourceIdentity != null || destinationIdentity == null) {
            throw new InvalidTransactionException(
                    "RENDIMENTO deve possuir apenas conta destino"
            );
        }
    }

    private static void validateAmount(Money amount) {

        if(amount == null){
            throw new InvalidTransactionException("Valor não pode ser null");
        }

        if (amount.isNegativeOrZero()) {
            throw new InvalidTransactionException(
                    "Valor deve ser maior que zero"
            );
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
    // Equals e Hashcode
    // =========================

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}