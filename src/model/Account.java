package model;

import exception.InvalidAmountException;
import exception.InsufficientBalanceException;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Account {

    private final UUID id;
    private final UUID clientId;
    private final AccountIdentity accountIdentity;
    private final LocalDateTime creationTime;

    private Money balance;

    protected Account(
            UUID clientId,
            AccountIdentity accountIdentity,
            Clock clock
    ) {

        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.accountIdentity = accountIdentity;
        this.creationTime = LocalDateTime.now(clock);
        this.balance = Money.ZERO;
    }

    public void deposit(Money amount) {

        validatePositiveAmount(amount);

        increaseBalance(amount);
    }

    public void withdraw(Money amount) {

        validatePositiveAmount(amount);

        decreaseBalance(amount);
    }

    protected final void increaseBalance(Money amount) {
        balance = balance.add(amount);
    }

    protected final void decreaseBalance(Money amount) {

        Money newBalance = balance.subtract(amount);

        if (newBalance.compareTo(minimumAllowedBalance()) < 0) {
            throw new InsufficientBalanceException(
                    "Saldo insuficiente"
            );
        }

        balance = newBalance;
    }

    protected abstract Money minimumAllowedBalance();

    private void validatePositiveAmount(Money amount) {

        if (amount.isNegativeOrZero()) {
            throw new InvalidAmountException(
                    "Valor inválido"
            );
        }
    }

    public boolean canBeRemoved() {
        return balance.isZero();
    }

    public UUID getId() {
        return id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public AccountIdentity getAccountIdentity() {
        return accountIdentity;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public Money getBalance() {
        return balance;
    }
}