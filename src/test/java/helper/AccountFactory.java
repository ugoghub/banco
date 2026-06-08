package helper;

import model.CheckingAccount;
import model.SavingsAccount;
import model.valueobject.AccountIdentity;

import java.time.Clock;
import java.util.UUID;

public final class AccountFactory {

    private AccountFactory() {
    }

    public static CheckingAccount checking(Clock clock) {
        return new CheckingAccount(
                UUID.randomUUID(),
                new AccountIdentity("01", "123456-1"),
                clock
        );
    }

    public static SavingsAccount savings(Clock clock) {
        return new SavingsAccount(
                UUID.randomUUID(),
                new AccountIdentity("01", "123456-1"),
                clock
        );
    }
}
