package model;

import exception.*;
import model.valueobject.AccountIdentity;
import model.valueobject.Money;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
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

        if(clientId == null){
            throw new InvalidClientIdException("ID do cliente não pode ser null");
        }
        if(accountIdentity == null){
            throw new InvalidAccountIdentityException(
                    "Conta inválida"
            );
        }
        if (clock == null) {
            throw new InvalidClockException("Horário inválido");
        }

        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.accountIdentity = accountIdentity;
        this.creationTime = LocalDateTime.now(clock);
        this.balance = Money.ZERO;
    }

    // =========================
    // Actions
    // =========================

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

    private void validatePositiveAmount(Money amount) {

        if(amount == null){
            throw new InvalidAmountException(
                    "Valor não pode ser null"
            );
        }

        if (amount.isNegativeOrZero()) {
            throw new InvalidAmountException(
                    "Valor deve ser maior que zero"
            );
        }
    }

    protected abstract Money minimumAllowedBalance();

    public boolean canBeRemoved() {
        return balance.isZero();
    }


    // =========================
    // Getters
    // =========================

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


    // =========================
    // Equals e Hashcode
    // =========================

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}