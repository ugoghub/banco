package model;

import exception.InsufficientBalanceException;
import exception.InvalidAmountException;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


public class SavingsAccount extends Account {
    private LocalDateTime lastInterestApply; // data da ultima vez da aplicação de juros
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.005"); // valor juros

    public SavingsAccount(UUID clientId, AccountIdentity accountIdentity, AccountType accountType) {
        super(clientId, accountIdentity, accountType);
        this.lastInterestApply = this.getCreationTime();
    }

    @Override
    public Transaction withdraw(Money value) {

        if (value.isNegativeOrZero())
            throw new InvalidAmountException("Valor inválido");

        if(value.isGreaterThan(getBalance())) {
            throw new InsufficientBalanceException("Saldo Insuficiente");
        }

        this.decreaseBalance(value);
        return new Transaction(TransactionType.WITHDRAW, value, this.getAccountIdentity(), null);
    }

    public boolean isTimeToApplyInterest() {
        return !lastInterestApply.plusMonths(1).isAfter(LocalDateTime.now());
    }

    public boolean applyInterest(){
        if(!isTimeToApplyInterest()) return false;

        Money interest = getBalance().multiply(INTEREST_RATE);

        this.increaseBalance(interest);

        //adicionar transaction

        lastInterestApply = LocalDateTime.now();
        return true;
    }
}