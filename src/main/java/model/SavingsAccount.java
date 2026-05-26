package model;

import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public class SavingsAccount extends Account {

    private static final BigDecimal INTEREST_RATE =
            new BigDecimal("0.005");

    private LocalDateTime lastInterestApply;

    public SavingsAccount(
            UUID clientId,
            AccountIdentity accountIdentity,
            Clock clock
    ) {

        super(
                clientId,
                accountIdentity,
                clock
        );

        this.lastInterestApply = getCreationTime();
    }

    @Override
    protected Money minimumAllowedBalance() {
        return Money.ZERO;
    }

    public boolean isTimeToApplyInterest(Clock clock) {

        return !lastInterestApply
                .plusMonths(1)
                .isAfter(LocalDateTime.now(clock));
    }

    public boolean applyInterest(Clock clock) {

        if (!isTimeToApplyInterest(clock)) {
            return false;
        }

        if (getBalance().isZero()) {
            lastInterestApply = LocalDateTime.now(clock); //registra tentativa de aplicar juros mesmo com saldo zero
            return false;
        }

        Money interest =
                getBalance().multiply(INTEREST_RATE);

        increaseBalance(interest);

        lastInterestApply = LocalDateTime.now(clock);

        return true;
    }
}