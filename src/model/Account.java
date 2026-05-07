package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import exception.*;


public abstract class Account {
    protected String clientCpf;
    protected final UUID id;
    protected String branch;
    protected BigDecimal balance;
    protected final LocalDateTime creationTime;
    protected final List<Transaction> transactionHistory;

    public Account(String clientCpf) {
        this.id = UUID.randomUUID();
        this.clientCpf = clientCpf;
        this.branch = "0001";
        this.balance = BigDecimal.ZERO;
        this.creationTime = LocalDateTime.now();
        this.transactionHistory = new ArrayList<>();
    }
    public void deposit(BigDecimal value) throws InvalidAmountException {
        if (value.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidAmountException("Valor inválido");
        this.balance = this.balance.add(value).setScale(2, RoundingMode.HALF_UP);
    }

    public void addTransaction(Transaction transaction) {
        this.transactionHistory.add(transaction);
    }
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }
    public abstract void withdraw(BigDecimal value) throws InvalidAmountException, InsufficientBalanceException;

    public String getClientCpf() { return clientCpf; }

    public UUID getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    public LocalDateTime getCreationTime() {
        return creationTime;
    }
    @Override
    public String toString() {
        return getAccountType() + " (ID: " + this.id + ")";
    }
    public abstract String getAccountType();
}