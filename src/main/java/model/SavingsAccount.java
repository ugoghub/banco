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

    private LocalDateTime lastInterestAppliedAt;

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

        this.lastInterestAppliedAt = getCreationTime();
    }

    @Override
    protected Money minimumAllowedBalance() {
        return Money.ZERO;
    }

    private boolean isTimeToApplyInterest(Clock clock) {

        return !lastInterestAppliedAt
                .plusMonths(1)
                .isAfter(LocalDateTime.now(clock));
    }

    public int applyPendingInterests(Clock clock) {

        int appliedPeriods = 0;

        while (isTimeToApplyInterest(clock)) {

            if (!getBalance().isZero()) {

                Money interest =
                        getBalance().multiplyByRate(INTEREST_RATE);

                increaseBalance(interest);

                appliedPeriods++;
            }

            lastInterestAppliedAt =
                    lastInterestAppliedAt.plusMonths(1);
        }

        return appliedPeriods;
    }
}