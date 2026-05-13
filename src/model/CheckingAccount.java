package model;

import exception.InsufficientBalanceException;
import exception.InvalidAmountException;
import model.valueObject.AccountIdentity;

import java.math.BigDecimal;
import java.util.UUID;


public class CheckingAccount extends Account{
    private static final BigDecimal WITHDRAW_LIMIT = new BigDecimal("1000");

    public CheckingAccount(UUID clientId, AccountIdentity accountIdentity) {
        super(clientId, accountIdentity);
    }

    @Override
    public void withdraw(BigDecimal value)
            throws InvalidAmountException, InsufficientBalanceException {

        if (value.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidAmountException("Valor inválido");

        BigDecimal available = getBalance().add(WITHDRAW_LIMIT);
        if (available.compareTo(value) < 0) throw new InsufficientBalanceException("Saldo Insuficiente");

        this.decreaseBalance(value);
    }

    @Override
    public String getAccountType() {
        return "Conta Corrente";
    }
}