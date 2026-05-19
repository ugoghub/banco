package model;

import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.UUID;

public class CheckingAccount extends Account {

    private static final Money OVERDRAFT_LIMIT =
            new Money(new BigDecimal("1000"));

    public CheckingAccount(
            UUID clientId,
            AccountIdentity accountIdentity,
            Clock clock
    ) {

        super(
                clientId,
                accountIdentity,
                clock
        );
    }

    @Override
    protected Money minimumAllowedBalance() {
        return OVERDRAFT_LIMIT.negate();
    }
}