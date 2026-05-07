package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import exception.*;


public class CheckingAccount extends Account{
    private static final BigDecimal WITHDRAW_LIMIT = new BigDecimal("1000");

    public CheckingAccount(String clientCpf) {
        super(clientCpf);
    }

    @Override
    public void withdraw(BigDecimal value) throws InvalidAmountException, InsufficientBalanceException {
        if (value.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidAmountException("Valor inválido");
        BigDecimal available = balance.add(WITHDRAW_LIMIT);
        if (available.compareTo(value) < 0) throw new InsufficientBalanceException("Saldo Insuficiente");

        balance = balance.subtract(value).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getAccountType() {
        return "Conta Corrente";
    }
}