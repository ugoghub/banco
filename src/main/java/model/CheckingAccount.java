package model;

import model.valueobject.AccountIdentity;
import model.valueobject.Money;

import java.time.Clock;
import java.util.UUID;

public class CheckingAccount extends Account {

    private static final Money OVERDRAFT_LIMIT =
            Money.of("1000");

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