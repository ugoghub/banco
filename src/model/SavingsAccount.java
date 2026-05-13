package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import exception.*;


public class SavingsAccount extends Account {
    private LocalDateTime lastInterestApply; // data da ultima vez da aplicação de juros
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.005");

    public SavingsAccount(UUID clientId) {
        super(clientId);
        this.lastInterestApply = this.creationTime;
    }

    @Override
    public void withdraw(BigDecimal value)
            throws InvalidAmountException, InsufficientBalanceException {

        if (value.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidAmountException("Valor inválido");

        if(value.compareTo(balance) > 0) {
            throw new InsufficientBalanceException("Saldo Insuficiente");
        }


        balance = balance.subtract(value).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isTimeToApplyInterest() {
        return !lastInterestApply.plusMonths(1).isAfter(LocalDateTime.now());
    }

    public boolean applyInterest(){
        if(!isTimeToApplyInterest()) return false;

        BigDecimal interest = this.balance.multiply(INTEREST_RATE);

        this.balance = this.balance.add(interest)
                        .setScale(2, RoundingMode.HALF_UP);

        addTransaction(new Transaction(TypeTransaction.INTEREST, interest, null,this.getId()));
        lastInterestApply = LocalDateTime.now();
        return true;
    }

    @Override
    public String getAccountType() {
        return "Conta Poupança";
    }
}