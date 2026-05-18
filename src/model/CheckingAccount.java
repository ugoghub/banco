package model;

import exception.InsufficientBalanceException;
import exception.InvalidAmountException;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;

import java.math.BigDecimal;
import java.util.UUID;


public class CheckingAccount extends Account{
    private static final Money WITHDRAW_LIMIT = new Money(new BigDecimal("1000"));

    public CheckingAccount(UUID clientId, AccountIdentity accountIdentity, AccountType accountType) {
        super(clientId, accountIdentity, accountType);
    }

    @Override
    public Transaction withdraw(Money value) {

        if (value.isNegativeOrZero())
            throw new InvalidAmountException("Valor inválido");

        Money available = getBalance().add(WITHDRAW_LIMIT);
        if (value.isGreaterThan(available)) throw new InsufficientBalanceException("Saldo Insuficiente");

        this.decreaseBalance(value);
        return new Transaction(TransactionType.WITHDRAW, value, this.getAccountIdentity(), null);
    }
}