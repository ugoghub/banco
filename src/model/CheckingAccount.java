package model;

import exception.InsufficientBalanceException;
import exception.InvalidAmountException;
import model.valueObject.AccountIdentity;
import model.valueObject.Money;

import java.math.BigDecimal;
import java.util.UUID;


public class CheckingAccount extends Account{
    private static final Money WITHDRAW_LIMIT = new Money(new BigDecimal("1000"));

    public CheckingAccount(UUID clientId, AccountIdentity accountIdentity) {
        super(clientId, accountIdentity);
    }

    @Override
    public void withdraw(Money value) {

        if (value.isNegative() || value.isZero())
            throw new InvalidAmountException("Valor inválido");

        Money available = getBalance().add(WITHDRAW_LIMIT);
        if (!available.isGreaterThanOrEqual(value)) throw new InsufficientBalanceException("Saldo Insuficiente");

        this.decreaseBalance(value);
    }
}