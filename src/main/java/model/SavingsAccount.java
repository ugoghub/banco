package model;

import model.valueobject.AccountIdentity;
import model.valueobject.Money;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public List<Money> applyPendingInterests(Clock clock) {

        List<Money> appliedInterests = new ArrayList<>();

        while (isTimeToApplyInterest(clock)) {

            if (getBalance().isGreaterThan(Money.ZERO)) {

                Money interest =
                        getBalance().multiplyByRate(INTEREST_RATE);

                increaseBalance(interest);

                appliedInterests.add(interest);
            }

            lastInterestAppliedAt =
                    lastInterestAppliedAt.plusMonths(1);
        }

        return appliedInterests;
    }
}