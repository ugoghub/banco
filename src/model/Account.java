package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import exception.*;
import model.valueObject.AccountIdentity;


public abstract class Account {
    private final UUID clientId;
    protected final UUID id;
    private final AccountIdentity accountIdentity;
    private BigDecimal balance;
    private final LocalDateTime creationTime;
    private final List<Transaction> transactionHistory;

    public Account(UUID clientId, AccountIdentity accountIdentity) {
        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.accountIdentity = accountIdentity;
        this.balance = BigDecimal.ZERO;
        this.creationTime = LocalDateTime.now();
        this.transactionHistory = new ArrayList<>();
    }
    public void deposit(BigDecimal value)
            throws InvalidAmountException {

        if (value.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidAmountException("Valor inválido");

        increaseBalance(value);
    }

    public boolean accountCanBeRemoved(){
        BigDecimal balance = getBalance();

        return balance.compareTo(BigDecimal.ZERO) == 0;
    }

    public void addTransaction(Transaction transaction) {
        this.transactionHistory.add(transaction);
    }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    public abstract void withdraw(BigDecimal value) throws InvalidAmountException, InsufficientBalanceException;

    public UUID getClientId() { return clientId; }

    public UUID getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    protected void increaseBalance(BigDecimal value){
        this.balance = this.balance.add(value).setScale(2, RoundingMode.HALF_UP);
    }
    protected void decreaseBalance(BigDecimal value){
        this.balance = this.balance.subtract(value).setScale(2, RoundingMode.HALF_UP);
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return getAccountType() + " | Ag: " + accountIdentity.branch() + " | Conta: " + accountIdentity.accountNumber();
    }

    public abstract String getAccountType();

    public AccountIdentity getAccountIdentity() {
        return accountIdentity;
    }
}