package model;

import exception.InvalidAmountException;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;

import java.time.LocalDateTime;
import java.util.UUID;


public abstract class Account {
    private final UUID clientId;
    protected final UUID id;
    private final AccountIdentity accountIdentity;
    private Money balance;
    private final LocalDateTime creationTime;
    private final AccountType accountType;

    public Account(UUID clientId, AccountIdentity accountIdentity, AccountType accountType) {
        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.accountIdentity = accountIdentity;
        this.accountType = accountType;
        this.balance = Money.ZERO;
        this.creationTime = LocalDateTime.now();
    }
    public Transaction deposit(Money value) {

        if (value.compareTo(Money.ZERO) <= 0)
            throw new InvalidAmountException("Valor inválido");

        increaseBalance(value);
        return new Transaction(TransactionType.DEPOSIT, value, null, this.getAccountIdentity());
    }

    public boolean canBeRemoved(){
        return balance.isZero();
    }

    public abstract Transaction withdraw(Money value);

    public UUID getClientId() { return clientId; }

    public UUID getId() {
        return id;
    }

    public Money getBalance() {
        return balance;
    }

    protected void increaseBalance(Money value){
        this.balance = this.balance.add(value);
    }
    protected void decreaseBalance(Money value){
        this.balance = this.balance.subtract(value);
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return accountType.getDescription() + " | " + accountIdentity;
    }
    public AccountIdentity getAccountIdentity() {
        return accountIdentity;
    }
}